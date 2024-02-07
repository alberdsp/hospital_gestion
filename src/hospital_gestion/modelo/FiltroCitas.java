/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital_gestion.modelo;

import java.util.Date;

/**
 *  Clase para definir los filtros de citas
 * @author Alber
 */
public class FiltroCitas {
    
    private String idpaciente;
    
    private String idmedico;
    
    private String fechacita;
    
   
    
    // constructor defrecto
    
    public FiltroCitas(){
    }

    public String getIdpaciente() {
        return idpaciente;
    }

    public void setIdpaciente(String idpaciente) {
        this.idpaciente = idpaciente;
    }

    public String getIdmedico() {
        return idmedico;
    }

    public void setIdmedico(String idmedico) {
        this.idmedico = idmedico;
    }

    public String getFechaCita() {
        return fechacita;
    }

    public void setFechaCita(Date fechafiltro) {
        this.fechacita = fechafiltro.toString();
    }
    
    
    
    
    
}
