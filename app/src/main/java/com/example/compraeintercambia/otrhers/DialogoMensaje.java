package com.example.compraeintercambia.otrhers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

public class DialogoMensaje extends DialogFragment {

    private String mensaje;

    public DialogoMensaje(String mensaje) {
        this.mensaje = mensaje;
    }




    //create dialog


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder dialogo = new AlertDialog.Builder(getActivity());
        dialogo.setMessage(mensaje);

        //add button
        dialogo.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                respuesta.confirmar(DialogoMensaje.this);
            }
        });
        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                respuesta.cancelar(DialogoMensaje.this);
            }
        });

        return dialogo.create();

    }


    //interface to process answers
    public interface Respuesta{
        public void confirmar(DialogFragment dialogo);
        public void cancelar(DialogFragment dialogo);
    }

    private Respuesta respuesta;

    public void procesarRespuesta(Respuesta res){
        respuesta=res;
    }

}
