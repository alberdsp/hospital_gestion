/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital_gestion.modelo;

/**
 *
 * @author Alber 2024
 */
public class FiltroPacientes {
    
    private String nombre;
    
    private String apellido1;
    
    private String dni;
    
   
    
    // constructor defecto
    
    public FiltroPacientes(){
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
    
    
    
    
    
}
