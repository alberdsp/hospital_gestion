package hospital_gestion.modelo;
// Generated 02-feb-2024 1:53:02 by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Citas generated by hbm2java
 */
public class Citas  implements java.io.Serializable {


     private Long id;
     private Medicos medicos;
     private Pacientes pacientes;
     private Date fecha;
     private Long enfermeroId;


    public Citas() {
    }

	
    public Citas(Date fecha) {
        this.fecha = fecha;
    }
    public Citas(Medicos medicos, Pacientes pacientes, Date fecha) {
       this.medicos = medicos;
       this.pacientes = pacientes;
       this.fecha = fecha;
      
 
    }
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public Medicos getMedicos() {
        return this.medicos;
    }
    
    public void setMedicos(Medicos medicos) {
        this.medicos = medicos;
    }
    public Pacientes getPacientes() {
        return this.pacientes;
    }
    
    public void setPacientes(Pacientes pacientes) {
        this.pacientes = pacientes;
    }
    public Date getFecha() {
        return this.fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public Long getEnfermeroId() {
        return this.enfermeroId;
    }
    

}


