package br.edu.unoesc.shoplist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import br.edu.unoesc.shoplist.adapter.ProdutosAdapter;
import br.edu.unoesc.shoplist.helper.DatabaseHelper;
import br.edu.unoesc.shoplist.model.Produto;


public class ComprasActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText edtDescricao;
    private EditText edtMarca;
    private EditText edtQtde;
    private Spinner spnUnidadeMedida;
    private Button btnSalvar;
    private Button btnLimpar;
    private ListView lstCompras;

    private DatabaseHelper dbHelper;

    private ProdutosAdapter produtosAdapter;
    private TabHost tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras);
        inicializaAbas();
        inicializarComponente();

        dbHelper = new DatabaseHelper(this);


        // criando o adaptador para unidades de medida
        ArrayAdapter<String> adaptadorUnidade = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.unidades));

        // vinculamos o adaptador ao spinner
        spnUnidadeMedida.setAdapter(adaptadorUnidade);
        try {
            atualizaListaCompra();

        } catch (SQLException ex) {
            System.out.print("Ocorreu um erro ao adquirir os produtos, erro :" + ex.getMessage());
        }

        //registrando a lista para o menu de contexto
        registerForContextMenu(lstCompras);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //carregar o menu de contexto da lista
        getMenuInflater().inflate(R.menu.menu_lista_compras, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.mnRemover: {
                Produto p = produtosAdapter.getItem(info.position);

                // TODO 1) (0,75) Abrir caixa de dialogo solicitando confirmacao para exclusão

                try {
                    dbHelper.getSimpleDao(Produto.class).delete(p);
                    atualizaListaCompra();

                    Toast.makeText(this, "Produto " + p.getDescricao() + " removido com sucesso...", Toast.LENGTH_LONG).show();
                } catch (SQLException e) {
                    Log.e("ERR_ANDROIDSHOP", e.getMessage());
                    // TODO 2) Tratar o erro de exclusão(mostrar mensagem)
                }
                // TODO 3) Mostrar Toast com mensagem de exclusão (Exclusão realizada com sucesso)

                break;
            }
            case R.id.mnRemoverTodos: {
                // TODO 4) Remover todos os produtos da lista

                try {
                    List<Produto> produtoList = dbHelper.getDao(Produto.class).queryForAll();
                    if ((produtoList != null) && (!produtoList.isEmpty())) {
                        dbHelper.getSimpleDao(Produto.class).delete(produtoList);
                    }
                } catch (SQLException e) {
                    Log.e("ERR_ANDROIDSHOP", e.getMessage());
                }

                break;
            }
        }

        return super.onContextItemSelected(item);
    }

    private void atualizaListaCompra() throws SQLException {
        produtosAdapter = new ProdutosAdapter(this, R.layout.lista_produtos, dbHelper.getDao(Produto.class).queryForAll());
        lstCompras.setAdapter(produtosAdapter);
        produtosAdapter.setNotifyOnChange(true);
        produtosAdapter.notifyDataSetChanged();
    }

    private void inicializarComponente() {
        edtDescricao = (EditText) findViewById(R.id.edtDescricao);
        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtQtde = (EditText) findViewById(R.id.edtQuantidade);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(this);
        btnLimpar = (Button) findViewById(R.id.btnLimpar);
        btnLimpar.setOnClickListener(this);
        lstCompras = (ListView) findViewById(R.id.lstCompras);

        // vincular o spnUnidadeMedida com o objeto de tela
        spnUnidadeMedida =
                (Spinner) findViewById(R.id.spnUnidadeMedida);
    }

    private void inicializaAbas() {
        // lidando com as abas da tela
        tabs = (TabHost) findViewById(android.R.id.tabhost);
        tabs.setup();

        // vinculando a aba 1
        TabHost.TabSpec spec = tabs.newTabSpec("tagCompras");
        spec.setContent(R.id.tabCompras);
        // descricao e icone da aba
        //spec.setIndicator(getString(R.string.tabCompras),
        // TODO: Conferir getDrawable para abas
        //      getResources().getDrawable(R.mipmap.ic_launcher));
        spec.setIndicator(getTabIndicatorView(this, getString(R.string.tabCompras),
                R.mipmap.ic_launcher));
        tabs.addTab(spec);

        // vinculando a aba 2
        spec = tabs.newTabSpec("tagCadastro");
        spec.setContent(R.id.tabCadastro);
        // descricao e icone da aba
        spec.setIndicator(getString(R.string.tabCadastro),
                getResources().getDrawable(R.mipmap.ic_launcher));
        tabs.addTab(spec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compras, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.mnSair: {
                //alertDialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.mnSair);
                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sair();

                    }
                });
                dialog.setNegativeButton("Não", null);

                dialog.show();

                break;
            }

            case R.id.mnUsuario: {
                //chamar a tela de cadastro de usuario
                Intent it = new Intent(this, UsuarioActivity.class);
                startActivity(it);

                break;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private void sair() {
        // finalizar a Activity
        finish();
        // finalizar a app
        System.exit(0);
    }

    private View getTabIndicatorView(Context context, String tag, int drawable) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.tab_widget_custom, null);
        TextView tv = (TextView) view.findViewById(R.id.tabIndicatorText);
        tv.setText(tag);
        ImageView tabIndicatorIcon = (ImageView) view
                .findViewById(R.id.tabIndicatorIcon);
        tabIndicatorIcon.setBackgroundResource(drawable);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLimpar: {
                edtDescricao.setText("");
                spnUnidadeMedida.setSelection(0);
                edtMarca.setText("");
                edtQtde.setText("");
                edtDescricao.requestFocus();
            }
            break;
            case R.id.btnSalvar: {
                String descricao = edtDescricao.getText().toString();
                String unidadeMedida = spnUnidadeMedida.getSelectedItem().toString();
                String marca = edtMarca.getText().toString();
                Double qtde = new Double(edtQtde.getText().toString());

                Produto p = new Produto(descricao, marca, unidadeMedida, qtde);

                try {
                    dbHelper.getDao(Produto.class).create(p);
                    atualizaListaCompra();
                    // TODO 5) Implementar a limpeza dos campos caso o cadastro seja efetivado
                    tabs.setCurrentTab(0);

                    Toast.makeText(this, "Produto salvo com sucesso", Toast.LENGTH_LONG).show();
                } catch (SQLException ex) {
                    // TODO 6) Exibir uma mensagem ocorreu um erro ao tentar inserir um novo produto
                    Log.e("ERR_ANDROIDSHOP", ex.getMessage());
                }
            }
            break;
        }
    }
}
