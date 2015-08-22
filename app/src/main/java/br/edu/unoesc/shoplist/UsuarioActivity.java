package br.edu.unoesc.shoplist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import br.edu.unoesc.shoplist.helper.DatabaseHelper;
import br.edu.unoesc.shoplist.model.Usuario;
import br.edu.unoesc.staticResult.StaticResult;


public class UsuarioActivity extends ActionBarActivity implements View.OnClickListener {
    private static final long DOUBLE_PRESS_INTERVAL = 250;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private long lastPressTime;
    private String imagePath;
    private EditText edtLatitude;
    private EditText edtLongitude;
    private EditText edtNome;
    private EditText edtEmail;
    private ImageView imgFoto;
    private Button btnFoto;
    private Button btnMapa;
    private Button btnSalvarUsuario;
    private Button btnLimparCadastroUsuario;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrousuario);

        // TODO 8) (0,75) Quando entrar na tela carregar os dados do usuário se já houver cadastro
        //vincular os campos/views com a tela
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtLatitude = (EditText) findViewById(R.id.edtLatitude);
        edtLongitude = (EditText) findViewById(R.id.edtLongitude);
        imgFoto = (ImageView) findViewById(R.id.imgFoto);

        btnFoto = (Button) findViewById(R.id.btnFoto);
        btnMapa = (Button) findViewById(R.id.btnMapa);
        btnSalvarUsuario = (Button) findViewById(R.id.btnSalvarUsuario);
        btnLimparCadastroUsuario = (Button) findViewById(R.id.btnLimparCadastroUsuario);
        btnFoto.setOnClickListener(this);
        btnMapa.setOnClickListener(this);
        btnSalvarUsuario.setOnClickListener(this);
        btnLimparCadastroUsuario.setOnClickListener(this);

        // TODO 13) (0,75) Trocar a chamada da activity da câmera(captura) para quanto usuário utilizar o duplo clique sobre o imageview
        imgFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                long pressTime = System.currentTimeMillis();

                if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                lastPressTime = pressTime;
            }
        });


        carregarDadosUsuario();
    }

    private void carregarDadosUsuario() {
        try {
            dbHelper = new DatabaseHelper(this);
            List<Usuario> usuarioList = dbHelper.getDao(Usuario.class).queryForAll();
            if ((usuarioList != null) && (usuarioList.size() > 0)) {
                Usuario usuario = usuarioList.get(0);

                edtNome.setText(usuario.getNome());
                edtEmail.setText(usuario.getEmail());
                edtLatitude.setText(usuario.getLatitude());
                edtLongitude.setText(usuario.getLongitude());
                imgFoto.setImageBitmap(BitmapFactory.decodeFile(usuario.getPathImagem()));
            }
        } catch (SQLException e) {
            Log.e("ERR_ANDROIDSHOP", e.getMessage());
            Toast.makeText(this, R.string.carregar_usuario, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cadastro_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO 9) Implementar o botão voltar para tela anterior
        //noinspection SimplifiableIfStatement
        if (id == R.id.mnVoltar) {
            voltarCadastro();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFoto: {
                Intent itCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(itCamera, StaticResult.RR_CAMERA.getValue());

                break;
            }

            // TODO 10) (0,80) Implementar o botão salvar para os dados do usuário
            case R.id.btnSalvarUsuario:
                salvarUsuario();

                break;

            case R.id.btnLimparCadastroUsuario:
                limparFormulario();

                break;

            case R.id.btnMapa: {
                Intent itMapa = new Intent(this, MapsActivity.class);
                startActivityForResult(itMapa, StaticResult.RR_MAPS_LATITUDE_LONGITUDE.getValue());

                break;
            }
        }
    }

    private void limparFormulario() {
        edtNome.setText("");
        edtEmail.setText("");
        edtLatitude.setText("");
        edtLongitude.setText("");
        imgFoto.setImageBitmap(null);
    }

    private void voltarCadastro() {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it = new Intent(UsuarioActivity.this, ComprasActivity.class);
                startActivity(it);
                finish();
            }
        }, 3000);
    }

    private void salvarUsuario() {
        String latitude = edtLatitude.getText().toString();
        String longitude = edtLongitude.getText().toString();
        String pathImage = imagePath;
        String nome = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        Usuario usuario = new Usuario(1, nome, email, latitude, longitude, pathImage);

        try {

            dbHelper.getDao(Usuario.class).createOrUpdate(usuario);
            Toast.makeText(this, R.string.usuario_salvo, Toast.LENGTH_LONG).show();
        } catch (SQLException ex) {
            Log.e("ERR_ANDROIDSHOP", ex.getMessage());
            Toast.makeText(this, "Erro ao inserir novo usuario!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //tratamento para captura da foto
        if ((requestCode == StaticResult.RR_CAMERA.getValue()) && (resultCode == RESULT_OK)) {
            Bitmap foto = (Bitmap) data.getExtras().get("data");
            imgFoto.setImageBitmap(foto);
            salvarFotoDisco(foto);
        } else if ((requestCode == StaticResult.RR_MAPS_LATITUDE_LONGITUDE.getValue()) && (resultCode == RESULT_OK)) {
            //recuperar os valores
            Double latitude = data.getDoubleExtra("latitude", 0);
            Double longitude = data.getDoubleExtra("longitude", 0);

            //setando os valores nos campos da tela
            edtLatitude.setText(latitude.toString());
            edtLongitude.setText(longitude.toString());
        }
    }

    private void salvarFotoDisco(Bitmap foto) {
        //salvando o arquivo da imagem
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.JPEG, 60, b);
        File arquivoFoto = new File(Environment.getExternalStorageDirectory() + File.separator + "foto.jpg");
        try {
            arquivoFoto.createNewFile();
            FileOutputStream fo = new FileOutputStream(arquivoFoto.getAbsolutePath());
            fo.write(b.toByteArray());
            fo.close();
            imagePath = arquivoFoto.getAbsolutePath();
            fo = null;
            arquivoFoto = null;
        } catch (IOException e) {
            Log.e("ERR_ANDROIDSHOP", e.getMessage());
            Toast.makeText(this, "Erro ao salvar imagem!", Toast.LENGTH_SHORT).show();
        }
    }
}
