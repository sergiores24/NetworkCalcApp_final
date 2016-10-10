package co.edu.unitecnologica.networkcalcapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String a="", b="", o,op;
    private boolean operator=false, dot=false, num=false;
    private int[] numbers={R.id.bt_0,R.id.bt_1,R.id.bt_2,R.id.bt_3,R.id.bt_4,R.id.bt_5,R.id.bt_6,R.id.bt_7,R.id.bt_8,R.id.bt_9};
    private int[] operators={R.id.bt_sum,R.id.bt_res,R.id.bt_mult,R.id.bt_div};
    private TextView log,cDisplay;
    private String url="http://162.243.64.94/dm.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log=(TextView)findViewById(R.id.log);
        cDisplay=(TextView)findViewById(R.id.cDisplay);
        setNumericListeners();
        setOperatorListeners();
    }

    private void setNumericListeners(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button boton=(Button)view;
                if (operator){
                    b+=(String)boton.getText();
                    cDisplay.setText(a+op+b);
                }else{
                    a+=(String)boton.getText();
                    cDisplay.setText(a);
                }
                num=true;
            }
        };
        for(int i: numbers)
            findViewById(i).setOnClickListener(listener);
    }

    private void setOperatorListeners(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(num && !operator) {
                    Button boton = (Button) view;
                    op = (String) boton.getText();
                    if (op.equals("+")) {
                        o = "sum";
                    } else {
                        if (op.equals("-")) {
                            o = "res";
                        } else {
                            if (op.equals("*")) {
                                o = "mul";
                            }
                            else {
                                o = "div";
                            }
                        }
                    }
                    cDisplay.setText(a + op);
                    operator = true;
                    dot = false;
                }
                else if(!num)
                    log.append("Falta operando\n");
                else log.append("Ya hay un operador\n");
            }
        };

        for(int i: operators)
            findViewById(i).setOnClickListener(listener);

        findViewById(R.id.bt_c).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cDisplay.setText("");
                log.setText("Pantalla limpia\n");
                num=false;
                dot=false;
                operator=false;
                a="";b="";op="";o="";
            }
        });

        findViewById(R.id.bt_pnt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dot)
                    log.append("No puedes poner otro punto\n");
                else {
                    if (operator)
                        b += ".";
                    else a += ".";
                    cDisplay.append(".");
                    dot=true;
                }
            }
        });

        findViewById(R.id.bt_ig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log.append("Expresión: "+a+op+b+"\n");
                log.append("Para el servidor: o: "+o+", a: "+a+", b: "+b+"\n");
                if(!num&&!operator&&b.equals(""))
                    log.append("La expresión no es válida aún\n");
                else
                    igualOnline();
            }
        });
    }

    private void igualOnline(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        cDisplay.setText(response);
                        log.append("Respuesta del servidor: "+response+"\n");
                        dot=false;operator=false;num=false;
                        o="";op="";a="";b="";
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        log.append("Error de conexión\n");
                        log.append("Verifique su conexión e intente nuevamente\n");
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("o",o);
                params.put("a",a);
                params.put("b",b);

                return params;
            }
        };
        queue.add(postRequest);
    }

}
