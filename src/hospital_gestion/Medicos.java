/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital_gestion;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Alber
 */
@Entity
@Table(name = "medicos", catalog = "hospital", schema = "")
@NamedQueries({
    @NamedQuery(name = "Medicos.findAll", query = "SELECT m FROM Medicos m")
    , @NamedQuery(name = "Medicos.findById", query = "SELECT m FROM Medicos m WHERE m.id = :id")
    , @NamedQuery(name = "Medicos.findByNumeroColegiado", query = "SELECT m FROM Medicos m WHERE m.numeroColegiado = :numeroColegiado")
    , @NamedQuery(name = "Medicos.findByDni", query = "SELECT m FROM Medicos m WHERE m.dni = :dni")
    , @NamedQuery(name = "Medicos.findByNombre", query = "SELECT m FROM Medicos m WHERE m.nombre = :nombre")
    , @NamedQuery(name = "Medicos.findByApellido1", query = "SELECT m FROM Medicos m WHERE m.apellido1 = :apellido1")
    , @NamedQuery(name = "Medicos.findByApellido2", query = "SELECT m FROM Medicos m WHERE m.apellido2 = :apellido2")
    , @NamedQuery(name = "Medicos.findByTelefono", query = "SELECT m FROM Medicos m WHERE m.telefono = :telefono")
    , @NamedQuery(name = "Medicos.findBySexo", query = "SELECT m FROM Medicos m WHERE m.sexo = :sexo")
    , @NamedQuery(name = "Medicos.findByEspecialidadId", query = "SELECT m FROM Medicos m WHERE m.especialidadId = :especialidadId")
    , @NamedQuery(name = "Medicos.findByHorarioId", query = "SELECT m FROM Medicos m WHERE m.horarioId = :horarioId")
    , @NamedQuery(name = "Medicos.findByUserId", query = "SELECT m FROM Medicos m WHERE m.userId = :userId")
    , @NamedQuery(name = "Medicos.findByCreatedAt", query = "SELECT m FROM Medicos m WHERE m.createdAt = :createdAt")
    , @NamedQuery(name = "Medicos.findByUpdatedAt", query = "SELECT m FROM Medicos m WHERE m.updatedAt = :updatedAt")})
public class Medicos implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "numero_colegiado")
    private String numeroColegiado;
    @Basic(optional = false)
    @Column(name = "dni")
    private String dni;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "apellido1")
    private String apellido1;
    @Column(name = "apellido2")
    private String apellido2;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "sexo")
    private String sexo;
    @Basic(optional = false)
    @Column(name = "especialidad_id")
    private long especialidadId;
    @Column(name = "horario_id")
    private BigInteger horarioId;
    @Column(name = "user_id")
    private BigInteger userId;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Medicos() {
    }

    public Medicos(Long id) {
        this.id = id;
    }

    public Medicos(Long id, String numeroColegiado, String dni, String nombre, String apellido1, long especialidadId) {
        this.id = id;
        this.numeroColegiado = numeroColegiado;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.especialidadId = especialidadId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        Long oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    public void setNumeroColegiado(String numeroColegiado) {
        String oldNumeroColegiado = this.numeroColegiado;
        this.numeroColegiado = numeroColegiado;
        changeSupport.firePropertyChange("numeroColegiado", oldNumeroColegiado, numeroColegiado);
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        String oldDni = this.dni;
        this.dni = dni;
        changeSupport.firePropertyChange("dni", oldDni, dni);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        String oldNombre = this.nombre;
        this.nombre = nombre;
        changeSupport.firePropertyChange("nombre", oldNombre, nombre);
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        String oldApellido1 = this.apellido1;
        this.apellido1 = apellido1;
        changeSupport.firePropertyChange("apellido1", oldApellido1, apellido1);
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        String oldApellido2 = this.apellido2;
        this.apellido2 = apellido2;
        changeSupport.firePropertyChange("apellido2", oldApellido2, apellido2);
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        String oldTelefono = this.telefono;
        this.telefono = telefono;
        changeSupport.firePropertyChange("telefono", oldTelefono, telefono);
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        String oldSexo = this.sexo;
        this.sexo = sexo;
        changeSupport.firePropertyChange("sexo", oldSexo, sexo);
    }

    public long getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(long especialidadId) {
        long oldEspecialidadId = this.especialidadId;
        this.especialidadId = especialidadId;
        changeSupport.firePropertyChange("especialidadId", oldEspecialidadId, especialidadId);
    }

    public BigInteger getHorarioId() {
        return horarioId;
    }

    public void setHorarioId(BigInteger horarioId) {
        BigInteger oldHorarioId = this.horarioId;
        this.horarioId = horarioId;
        changeSupport.firePropertyChange("horarioId", oldHorarioId, horarioId);
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        BigInteger oldUserId = this.userId;
        this.userId = userId;
        changeSupport.firePropertyChange("userId", oldUserId, userId);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        Date oldCreatedAt = this.createdAt;
        this.createdAt = createdAt;
        changeSupport.firePropertyChange("createdAt", oldCreatedAt, createdAt);
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        Date oldUpdatedAt = this.updatedAt;
        this.updatedAt = updatedAt;
        changeSupport.firePropertyChange("updatedAt", oldUpdatedAt, updatedAt);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Medicos)) {
            return false;
        }
        Medicos other = (Medicos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hospital_gestion.Medicos[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
