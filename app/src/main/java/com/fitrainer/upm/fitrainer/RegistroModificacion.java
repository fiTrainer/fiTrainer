package com.fitrainer.upm.fitrainer;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fitrainer.upm.fitrainer.Sesion.SessionManagement;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegistroModificacion extends AppCompatActivity {
    ProgressDialog prgDialog;

    // Session Manager Class
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_modificacion);
        final Bundle extras = getIntent().getExtras();

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Por favor espere...");
        prgDialog.setCancelable(false);

        //Rellenamos el spinner
        List age = new ArrayList();
        age.add("Elija una edad");
        for (int i = 1; i <= 100; i++) {
            age.add(i);
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, age);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

       final Spinner spinner = (Spinner)findViewById(R.id.spinnerEdad);
        spinner.setAdapter(spinnerArrayAdapter);


        //Inicializacion EditableText
        final EditText etNickname = (EditText) findViewById(R.id.etNickname);
        final EditText etNombre = (EditText) findViewById(R.id.etNombre);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etContrasenia = (EditText) findViewById(R.id.etContrasenia);
        final EditText etRepContrasenia = (EditText) findViewById(R.id.etRepContrasenia);
        final EditText etAltura = (EditText) findViewById(R.id.etAltura);
        final EditText etPeso = (EditText) findViewById(R.id.etPeso);
        Button btnModReg = (Button)findViewById(R.id.btnModReg);
        final RadioButton rbMujer = (RadioButton) findViewById(R.id.rbtMujer);
        final RadioButton rbHombre= (RadioButton) findViewById(R.id.rbtHombre);
        Button btnReset = (Button)findViewById(R.id.btnReset);
        //final Usuario user= new Usuario(0, "pepe1993", "Pepe", "pepe@pepe.com", "contraseña", 23, 65.3, 1.73,true, false);

        if(extras.getBoolean("VIENE_DE_LOGIN")){
            btnModReg.setText("Registrarse");
        }else{
            // Session class instance
            session = new SessionManagement(getApplicationContext());
            //session.checkLogin();
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();


            etNickname.setEnabled(false);
            etNickname.setText(user.get(SessionManagement.KEY_NICKNAME));
            etNombre.setText(user.get(SessionManagement.KEY_NAME));
            etEmail.setText(user.get(SessionManagement.KEY_EMAIL));
            etContrasenia.setText(user.get(SessionManagement.KEY_CONTRASENIA));
            etRepContrasenia.setText(user.get(SessionManagement.KEY_CONTRASENIA));
            spinner.setSelection(Integer.parseInt(user.get(SessionManagement.KEY_EDAD)));
            etAltura.setText(user.get(SessionManagement.KEY_ALTURA));
            etPeso.setText(user.get(SessionManagement.KEY_PESO));
            if(Boolean.valueOf(user.get(SessionManagement.KEY_SEXO))){
                rbHombre.setChecked(true);
            }else{
                rbMujer.setChecked(true);
            }
            btnModReg.setText("Modificar");
        }
        //Para el boton de Registrarse o Modificar
        btnModReg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                boolean validar=true;
                boolean validarMod=true;
                RequestParams params = new RequestParams();
                if(etNombre.getText().toString().matches("")){
                    etNombre.setError("Debe ingresar un Nombre");
                    validar=false;
                    validarMod=false;
                }else{
                    //Agregar campos a los parametros de la llamada
                    params.put("nombre",etNombre.getText());

                }
                if(etEmail.getText().toString().matches("")){
                    etEmail.setError("Debe ingresar un Email");
                    validar=false;
                    validarMod=false;
                }else{
                    if (!etEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))
                    {
                        etEmail.setError("Email no valido");
                        validar=false;
                        validarMod=false;
                    }
                    else{
                        //Agregar campos a los parametros de la llamada
                        params.put("email",etEmail.getText());
                    }
                }
                if(etContrasenia.getText().toString().matches("")){
                    etContrasenia.setError("Debe ingresar una contraseña");
                    validar=false;
                }else{
                    if(etRepContrasenia.getText().toString().matches("")){
                        etRepContrasenia.setError("Debe repetir la contraseña");
                        validar=false;
                        validarMod=false;
                    }
                }
                if(!etRepContrasenia.getText().toString().matches("")&&!etContrasenia.getText().toString().matches("")){
                    if(!etContrasenia.getText().toString().matches(etRepContrasenia.getText().toString())){
                        validar=false;
                        validarMod=false;
                        etRepContrasenia.setError("Las contraseñas no concuerdan");
                    }
                    else{
                        //Agregar campos a los parametros de la llamada
                        int saltLength = 16; // same size as key output
                        SecureRandom random = new SecureRandom();
                        byte[] salt = new byte[saltLength];
                        random.nextBytes(salt);
                        String pass=etContrasenia.getText().toString()+salt;
                        String generatedPassword= sha256(pass);
                        params.put("password",generatedPassword);
                        params.put("salt",salt.toString());
                    }
                }
                if(etAltura.getText().toString().matches("")){
                    etAltura.setError("Debe insertar su altura");
                    validar=false;
                    validarMod=false;
                }else{
                    //Agregar campos a los parametros de la llamada
                    params.put("altura",etAltura.getText());
                }
                if(etPeso.getText().toString().matches("")){
                    etPeso.setError("Debe insertar su peso");
                    validar=false;
                    validarMod=false;
                }else{
                    //Agregar campos a los parametros de la llamada
                    params.put("peso",etPeso.getText());
                }
                if(spinner.getSelectedItemPosition()==0){
                    View selectedView = spinner.getSelectedView();
                    TextView selectedTextView = (TextView) selectedView;
                    selectedTextView.setError("Debe seleccionar una edad");
                    validar=false;
                    validarMod=false;
                }else{
                    //Agregar campos a los parametros de la llamada
                    params.put("edad",spinner.getSelectedItem().toString());
                }
                if(!rbMujer.isChecked()&&!rbHombre.isChecked()){
                    rbMujer.setError("Debe seleccionar un sexo");
                    rbHombre.setError("Debe seleccionar un sexo");
                    validar=false;
                    validarMod=false;
                }else{
                    rbMujer.setError(null);
                    rbHombre.setError(null);
                    if(rbHombre.isChecked()){
                        params.put("sexo",0);
                    }else{
                        params.put("sexo",1);
                    }

                }
                if(extras.getBoolean("VIENE_DE_LOGIN")){
                    if(etNickname.getText().toString().matches("")){
                        etNickname.setError("Debe ingresar un Nickname");
                        validar=false;
                    }else{
                        //Se debe buscar en la base de datos si existe ese nickname
                        //Agregar campos a los parametros de la llamada
                        params.put("nickname",etNickname.getText());
                    }
                    //Si valido hago insert
                    if(validar){
                        //Si se ha validado, entonces insertamos en BBDD
                        params.put("esEntrenador",0);
                        params.put("accion","registro");
                        invokeWS(params);
                        RegistroModificacion.super.onBackPressed();
                    }
                }else{
                    if(validarMod){
                        //Si se ha validado, entonces update en BBDD
                        params.put("nickname",etNickname.getText());
                        params.put("esEntrenador",0);
                        params.put("accion","modificarUsuario");
                        invokeWS(params);
                        //RegistroModificacion.super.onBackPressed();
                    }
                }


            }
        });

        //Para el boton de Reset
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(extras.getBoolean("VIENE_DE_LOGIN")){
                    etNickname.setText("");
                    etNombre.setText("");
                    etEmail.setText("");
                    etContrasenia.setText("");
                    etRepContrasenia.setText("");
                    etAltura.setText("");
                    etPeso.setText("");
                    spinner.setSelection(0);
                    rbHombre.setChecked(false);
                    rbMujer.setChecked(false);
                }else{
                    HashMap<String, String> user = session.getUserDetails();
                    etNickname.setEnabled(false);
                    etNickname.setText(user.get(SessionManagement.KEY_NICKNAME));
                    etNombre.setText(user.get(SessionManagement.KEY_NAME));
                    etEmail.setText(user.get(SessionManagement.KEY_EMAIL));
                    etContrasenia.setText(user.get(SessionManagement.KEY_CONTRASENIA));
                    etRepContrasenia.setText(user.get(SessionManagement.KEY_CONTRASENIA));
                    spinner.setSelection(Integer.parseInt(user.get(SessionManagement.KEY_EDAD)));
                    etAltura.setText(user.get(SessionManagement.KEY_ALTURA));
                    etPeso.setText(user.get(SessionManagement.KEY_PESO));
                    if(Boolean.valueOf(user.get(SessionManagement.KEY_SEXO))){
                        rbHombre.setChecked(true);
                        rbMujer.setChecked(false);
                    }else{
                        rbMujer.setChecked(true);
                        rbHombre.setChecked(false);
                    }
                }
            }
        });
    }


    public void onRadioButtonClicked(View v)
    {
        boolean  checked = ((RadioButton) v).isChecked();
        RadioButton rbMujer = (RadioButton) findViewById(R.id.rbtMujer);
        RadioButton rbHombre= (RadioButton) findViewById(R.id.rbtHombre);

        switch(v.getId()){

            case R.id.rbtMujer:
                if(checked)
                    rbHombre.setChecked(false);
                break;

            case R.id.rbtHombre:
                if(checked)
                    rbMujer.setChecked(false);
                break;


        }
    }
    //Obtener SHA-256 de un String
    private String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getString(R.string.serverURL),params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                   // System.out.println(new String(response));
                    JSONObject obj = new JSONObject(new String(response));
                    // When the JSON response has status boolean value assigned with true
                    if(obj.getBoolean("status")){
                        // Set Default Values for Edit View controls
                        // Display successfully registered message using Toast
                        Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                    // Else display error message
                    else{
                        //errorMsg.setText(obj.getString("error_msg"));
                        Toast.makeText(getApplicationContext(),obj.getString("Error") , Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                //When user is already registered
                else if(statusCode == 306){
                    Toast.makeText(getApplicationContext(), "El usuario ya se encuentra registrado", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
