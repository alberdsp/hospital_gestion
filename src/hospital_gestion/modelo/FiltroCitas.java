/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital_gestion.modelo;

/**
 *  Clase para definir los filtros de citas
 * @author Alber
 */
public class FiltroCitas {
    
    private Pacientes paciente;
    
    private Medicos medico;
    

    
   
    
    // constructor defrecto
    
    public FiltroCitas(){
    }

    public Pacientes getPaciente() {
        return paciente;
    }

    public void setPaciente(Pacientes paciente) {
        this.paciente = paciente;
    }

    public Medicos getMedico() {
        return medico;
    }

    public void setMedico(Medicos medico) {
        this.medico = medico;
    }
    
}
