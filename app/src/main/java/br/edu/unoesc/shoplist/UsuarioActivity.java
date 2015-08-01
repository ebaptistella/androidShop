package br.edu.unoesc.shoplist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import br.edu.unoesc.staticResult.StaticResult;


public class UsuarioActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText edtLatitude;
    private EditText edtLongitude;
    private ImageView imgFoto;
    private Button btnFoto;
    private Button btnMapa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrousuario);

        //vincular os campos/views com a tela
        edtLatitude = (EditText) findViewById(R.id.edtLatitude);
        edtLongitude = (EditText) findViewById(R.id.edtLongitude);
        imgFoto = (ImageView) findViewById(R.id.imgFoto);
        btnFoto = (Button) findViewById(R.id.btnFoto);
        btnMapa = (Button) findViewById(R.id.btnMapa);
        btnFoto.setOnClickListener(this);
        btnMapa.setOnClickListener(this);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.mnVoltar) {
            Toast.makeText(this, "Falta implementar", Toast.LENGTH_SHORT).show();

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

            case R.id.btnMapa: {
                Intent itMapa = new Intent(this, MapsActivity.class);
                startActivityForResult(itMapa, StaticResult.RR_MAPS_LATITUDE_LONGITUDE.getValue());

                break;
            }

            // TODO fazer o botao voltar
            // TODO fazer o botao salvar
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //tratamento para captura da foto
        if ((requestCode == StaticResult.RR_CAMERA.getValue()) && (resultCode == RESULT_OK)) {
            Bitmap foto = (Bitmap) data.getExtras().get("data");
            imgFoto.setImageBitmap(foto);

            //salvando o arquivo da imagem
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            foto.compress(Bitmap.CompressFormat.JPEG, 60, b);
            File arquivoFoto = new File(Environment.getExternalStorageDirectory() + File.separator + "foto.jpg");
            try {
                arquivoFoto.createNewFile();
                FileOutputStream fo = new FileOutputStream(arquivoFoto.getAbsolutePath());
                fo.write(b.toByteArray());
                fo.close();
                fo = null;
                arquivoFoto = null;
            } catch (IOException e) {
                Log.e("ERR_ANDROIDSHOP", e.getMessage());
            }
        } else if ((requestCode == StaticResult.RR_MAPS_LATITUDE_LONGITUDE.getValue()) && (resultCode == RESULT_OK)) {
            //recuperar os valores
            Double latitude = data.getDoubleExtra("latitude", 0);
            Double longitude = data.getDoubleExtra("longitude", 0);

            //setando os valores nos campos da tela
            edtLatitude.setText(latitude.toString());
            edtLongitude.setText(longitude.toString());
        }
    }
}
