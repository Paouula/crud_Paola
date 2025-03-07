package paola.rivera.crudpaolab2

import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.Clase_Conexion
import modelo.dataClassMascotas

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1. Mandar a llamar a todos los elementos
        val txtNombre = findViewById<EditText>(R.id.txtNombreMasc)
        val txtPeso = findViewById<EditText>(R.id.txtPeso)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rcvMascotas = findViewById<RecyclerView>(R.id.rcvMascotas)

        //Primer paso para mostrar datos
        //Asignarlo un layout al RecyclerView
        rcvMascotas.layoutManager = LinearLayoutManager(this)

        ///////////TODO: Mostrar datos //////////////
        //Funcion para mostrar los datos
        fun obtenerDatos():List<dataClassMascotas>{
            //1. Creo un objeto de la clase conexión
            val objConexion = Clase_Conexion().cadenaConexion()

            //2. Creo un Statement
            val statement = objConexion?.createStatement()
            val resulSet = statement?.executeQuery("Select * from tbMascotas") !!
            val mascotas = mutableListOf<dataClassMascotas>()

            //Recorro todos los registros de la base de datos
            while (resulSet.next()){
                val nombre = resulSet.getString("NombreMascota")
                val mascota = dataClassMascotas(nombre)
                mascotas.add(mascota)
            }
            return mascotas
        }

        //Asignar el adaptador al RecyclerView
        CoroutineScope(Dispatchers.IO).launch {
            val mascotasDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(mascotasDB)
                rcvMascotas.adapter = adapter
            }
        }





        //2.Programar el boton para agregar
        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //1.Creo un objeto de la clase conexión
                val objConexion = Clase_Conexion().cadenaConexion()

                //2.Creo una variable de cotenga un PrepareStatement
                val addMascota = objConexion?.prepareStatement("insert into tbMascotas values(?,?,? )")!!
                addMascota.setString( 1, txtNombre.text.toString())
                addMascota.setInt(2, txtPeso.text.toString().toInt())
                addMascota.setInt( 3, txtEdad.text.toString().toInt())

                addMascota.executeUpdate()
            }
        }

    }
}