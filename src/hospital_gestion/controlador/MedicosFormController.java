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
       
       cargarMedicosEnTabla( medicosForm.jTableMedicos); 
        
        configurarListeners();
    }

    // Método para guardar un médico en la base de datos
    public boolean guardarMedico(Medicos medico) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            
            
            
            if (medico.getId()==null){
            session.save(medico);
            }else{
       //      session.save(medico);
            session.merge(medico);
       
            }
            
                   
            transaction.commit();
            
            cargarMedicosEnTabla( medicosForm.jTableMedicos);
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                System.out.println(e);
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
           
           
           // Agregar un ActionListener para el botón "Grabar"
           
              
          // Agregar ActionListener a la tabla para detectar la selección de fila
            medicosForm.jTableMedicos.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                // Llamar al método para cargar datos cuando se selecciona una fila
                cargarFilaSelect();
            }
        });
           
           
           
    }
    
    
    

    /**
     * Método para eliminar un médico de la base de datos
     * @param id  pasamos el id a borrar
     * @return  true o false si fue exitoso
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

        // Define las cabeceras de la tabla
    String[] columnNames = {"ID", "Nombre", "Apellido1", "Apellido2", "Teléfono", "DNI", "Nº Colegiado"};
    model.setColumnIdentifiers(columnNames);
                
                
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
        String id = medicosForm.jTextFieldID.getText();
        String nombre = medicosForm.jTextFieldNombre.getText();
        String apellido1 = medicosForm.jTextFieldApellido1.getText();
        String apellido2 = medicosForm.jTextFieldApellido2.getText();
        String telefono = medicosForm.jTextFieldTelefono.getText();
        String dni = medicosForm.jTextFieldDni.getText();
        String numeroColegiado = medicosForm.jTextFieldNcolegiado.getText();
                       
        // Crear un objeto Medicos con los datos obtenidos
        
        Medicos medico = new Medicos();
        
        medico.setId(Long.parseLong(id));
        medico.setNombre(nombre);
        medico.setApellido1(apellido1);
        medico.setApellido2(apellido2);
        medico.setTelefono(telefono);
        medico.setDni(dni);
        medico.setNumeroColegiado(numeroColegiado);

        return medico;
    }
    
    
    // Método para cargar los datos de la fila seleccionada en los campos de edición
    public void cargarFilaSelect() {
        
        
    int selectedRow = medicosForm.jTableMedicos.getSelectedRow();

    // Verifica si se ha seleccionado una fila
    if (selectedRow != -1) {
        DefaultTableModel model = (DefaultTableModel) medicosForm.jTableMedicos.getModel();

        // Obtiene los valores de las columnas de la fila seleccionada
        Object idValue = model.getValueAt(selectedRow, 0);
        Object nombreValue = model.getValueAt(selectedRow, 1);
        Object apellido1Value = model.getValueAt(selectedRow, 2);
        Object apellido2Value = model.getValueAt(selectedRow, 3);
        Object telefonoValue = model.getValueAt(selectedRow, 4);
        Object dniValue = model.getValueAt(selectedRow, 5);
        Object ncolegiadoValue = model.getValueAt(selectedRow, 6);

        // Establece los valores en los campos de edición
        medicosForm.jTextFieldID.setText(String.valueOf(idValue));
        medicosForm.jTextFieldNombre.setText(String.valueOf(nombreValue));
        medicosForm.jTextFieldApellido1.setText(String.valueOf(apellido1Value));
        medicosForm.jTextFieldApellido2.setText(String.valueOf(apellido2Value));
        medicosForm.jTextFieldTelefono.setText(String.valueOf(telefonoValue));
        medicosForm.jTextFieldDni.setText(String.valueOf(dniValue));
        medicosForm.jTextFieldNcolegiado.setText(String.valueOf(ncolegiadoValue));
    }
}

    
}
