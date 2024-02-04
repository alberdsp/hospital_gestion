package hospital_gestion.controlador;

import hospital_gestion.modelo.Medicos;
import hospital_gestion.vista.MedicosForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class MedicosFormController {

    private SessionFactory sessionFactory;
    private final MedicosForm medicosForm;

    public MedicosFormController(MedicosForm medicosForm) {
        this.medicosForm = medicosForm;
          
       
        
        // Inicializa la sesión de Hibernate
        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(Medicos.class)
                .buildSessionFactory();
    }

    // Método para guardar un médico en la base de datos
    public boolean guardarMedico(Medicos medico) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(medico);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    
    
    private void configurarListeners() {

            // Agregar un ActionListener para el botón "Grabar"
           medicosForm.jButtonGrabar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // Obtenemos los datos y grabamos
                Medicos medico = obtenerDatosDelFormulario();
                boolean exito = guardarMedico(medico);
                
                // comprobamos si se grabó
                if (exito) {
                    // Éxito:
                } else {
                    // Error: 
                }
            }
        });
           
    }
    
    
    // Método para eliminar un médico de la base de datos

    /**
     *
     * @param id
     * @return
     */
    
    public boolean eliminarMedico(long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Medicos medico = (Medicos) session.get(Medicos.class, id);
            if (medico != null) {
                session.delete(medico);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    // Método para cargar la lista de médicos en una tabla
    public void cargarMedicosEnTabla(JTable table) {
        Session session = sessionFactory.openSession();
        List<Medicos> medicosList = session.createQuery("from Medicos").list();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Medicos medico : medicosList) {
            Object[] row = {
                    medico.getId(),
                    medico.getNombre(),
                    medico.getApellido1(),
                    medico.getApellido2(),
                    medico.getTelefono(),
                    medico.getDni(),
                    medico.getNumeroColegiado()
            };
            model.addRow(row);
        }

        session.close();
    }
    
    
        // Método para obtener los datos del formulario y crear un objeto Medicos
    private Medicos obtenerDatosDelFormulario() {
        // Obtener los valores de los campos de texto u otros componentes del formulario
        String nombre = medicosForm.jTextFieldNombre.getText();
        String apellido1 = medicosForm.jTextFieldApellido1.getText();
        String apellido2 = medicosForm.jTextFieldApellido2.getText();
        String telefono = medicosForm.jTextFieldTelefono.getText();
        String dni = medicosForm.jTextFieldDni.getText();
        String numeroColegiado = medicosForm.jTextFieldNcolegiado.getText();
                       
        // Crear un objeto Medicos con los datos obtenidos
        Medicos medico = new Medicos();
        medico.setNombre(nombre);
        medico.setApellido1(apellido1);
        medico.setApellido2(apellido2);
        medico.setTelefono(telefono);
        medico.setDni(dni);
        medico.setNumeroColegiado(numeroColegiado);

        return medico;
    }
    
    
}
