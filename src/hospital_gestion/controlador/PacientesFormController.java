package hospital_gestion.controlador;

import hospital_gestion.modelo.FiltroPacientes;
import hospital_gestion.modelo.Pacientes;
import hospital_gestion.vista.PacientesForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


/**
 *  Clase que controla el formulario de Pacientes
 * @author Alber 2024
 */

public class PacientesFormController {

    private SessionFactory sessionFactory;
    private final PacientesForm pacientesForm;

    public PacientesFormController(PacientesForm pacientesForm) {
        this.pacientesForm = pacientesForm;

        // Inicializa la sesión de Hibernate
        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(Pacientes.class)
                .buildSessionFactory();

        cargarPacientesEnTabla(pacientesForm.jTablePacientes);

        configurarListeners();
    }

    // aquí configuramos todos los listeners de la aplicación
    private void configurarListeners() {

        // Agregar un ActionListener para el botón "Grabar"
        pacientesForm.jButtonGrabar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtenemos los datos y grabamos
                Pacientes paciente = obtenerDatosDelFormulario();
                boolean exito = guardarMedico(paciente);

                // comprobamos si se grabó
                if (exito) {
                    // Éxito:
                } else {
                    // Error: 
                }
            }
        });

        // Agregar un ActionListener para el botón "Filtrar"
        pacientesForm.jButtonFiltro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO   armar bien el filtro para pasar objeto con sus parámtros
                // armamos los filtros
                String nombre = pacientesForm.jTextFieldFiltroNombre.getText();
                String apellido1 = pacientesForm.jTextFieldFiltroApe1.getText();
                String dni = pacientesForm.jTextFieldFiltroDni.getText();

                // Reemplazamos las cadenas vacías con null
                nombre = (nombre.isEmpty()) ? null : nombre;
                apellido1 = (apellido1.isEmpty()) ? null : apellido1;
                dni = (dni.isEmpty()) ? null : dni;

                FiltroPacientes filtro = new FiltroPacientes();
                filtro.setNombre(nombre);
                filtro.setApellido1(apellido1);
                filtro.setDni(dni);

                // filtramos pasando el objeto con los filtros
                filtrarPacientes(filtro);
            }
        });
        
        
        
          // Agregar un ActionListener para el botón "Reset"
        pacientesForm.jButtonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setearFiltro();
            }

         
        });

        // Agregar un ActionListener para el botón "Nuevo"
        pacientesForm.jButtonNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setearForm();
            }
        });

        // Agregar un ActionListener para el botón "Borrar"
        pacientesForm.jButtonEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtenemos los datos y borramos
                Pacientes paciente = obtenerDatosDelFormulario();
                boolean exito = eliminarMedico(paciente);

                // comprobamos si se borró
                if (exito) {
                    // Éxito:
                } else {
                    // Error: 
                }
            }
        });

        // Agregar ActionListener a la tabla para detectar la selección de fila
        pacientesForm.jTablePacientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Llamar al método para cargar datos cuando se selecciona una fila
                cargarFilaSelect();
            }
        });

    }

    /**
     * Método para guardar en la base de datos o actualizar
     *
     * @param paciente recibe un objeto médico
     * @return devuelve tru o false si se guarda con éxito
     */
    public boolean guardarMedico(Pacientes paciente) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // si no tiene id entendemos que es nuevo
            if (paciente.getId() == null) {
                session.save(paciente);
            } else {
                // si tiene hacemos merge para update
                session.merge(paciente);

            }

            transaction.commit();

            cargarPacientesEnTabla(pacientesForm.jTablePacientes);

            JOptionPane.showMessageDialog(null, "Paciente grabado corréctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
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

    /**
     * Método para eliminar un médico de la base de datos
     *
     * @param id pasamos el id a borrar
     * @return true o false si fue exitoso
     */
    public boolean eliminarMedico(Pacientes paciente) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            if (paciente.getId() == null) {
                // no hacemos nada si es nulo
            } else {

                session.delete(paciente);
            }

            transaction.commit();

            cargarPacientesEnTabla(pacientesForm.jTablePacientes);

            JOptionPane.showMessageDialog(null, "Paciente borrado corréctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                System.out.println(e);
            }
            e.printStackTrace();
            // Mostrar un mensaje de alerta
            JOptionPane.showMessageDialog(null, "No se puede borrar el paciente, tiene citas asociadas", "Alerta", JOptionPane.WARNING_MESSAGE);
            return false;
        } finally {
            session.close();
        }
    }

    // Método para cargar la lista de médicos en una tabla
    public void cargarPacientesEnTabla(JTable table) {
        Session session = sessionFactory.openSession();
        List<Pacientes> pacientesList = session.createQuery("from Pacientes").list();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Define las cabeceras de la tabla
        String[] columnNames = {"ID", "Nombre", "Apellido1", "Apellido2", "Teléfono", "DNI", "SIP"};
        model.setColumnIdentifiers(columnNames);

        for (Pacientes paciente : pacientesList) {
            Object[] row = {
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido1(),
                paciente.getApellido2(),
                paciente.getTelefono(),
                paciente.getDni(),
                paciente.getSip(),
               
            };
            model.addRow(row);
        }

        session.close();
    }

    // Método para obtener los datos del formulario y crear un objeto Pacientes
    private Pacientes obtenerDatosDelFormulario() {
        // Obtener los valores de los campos de texto u otros componentes del formulario

        String id = pacientesForm.jTextFieldID.getText();
        String nombre = pacientesForm.jTextFieldNombre.getText();
        String apellido1 = pacientesForm.jTextFieldApellido1.getText();
        String apellido2 = pacientesForm.jTextFieldApellido2.getText();
        String telefono = pacientesForm.jTextFieldTelefono.getText();
        String dni = pacientesForm.jTextFieldDni.getText();
        String sip = pacientesForm.jTextFieldSip.getText();

        // Crear un objeto Pacientes con los datos obtenidos
        Pacientes paciente = new Pacientes();

        if (id.equals("")) {

        } else {
            paciente.setId(Long.parseLong(id));
        }
        paciente.setNombre(nombre);
        paciente.setApellido1(apellido1);
        paciente.setApellido2(apellido2);
        paciente.setTelefono(telefono);
        paciente.setDni(dni);
        paciente.setSip(sip);
    

        return paciente;
    }

    // Método para cargar los datos de la fila seleccionada en los campos de edición
    public void cargarFilaSelect() {

        int selectedRow = pacientesForm.jTablePacientes.getSelectedRow();

        // Verifica si se ha seleccionado una fila
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) pacientesForm.jTablePacientes.getModel();

            // Obtiene los valores de las columnas de la fila seleccionada
            Object idValue = model.getValueAt(selectedRow, 0);
            Object nombreValue = model.getValueAt(selectedRow, 1);
            Object apellido1Value = model.getValueAt(selectedRow, 2);
            Object apellido2Value = model.getValueAt(selectedRow, 3);
            Object telefonoValue = model.getValueAt(selectedRow, 4);
            Object dniValue = model.getValueAt(selectedRow, 5);
            Object sipValue = model.getValueAt(selectedRow, 6);

            // Establece los valores en los campos de edición
            pacientesForm.jTextFieldID.setText(String.valueOf(idValue));
            pacientesForm.jTextFieldNombre.setText(String.valueOf(nombreValue));
            pacientesForm.jTextFieldApellido1.setText(String.valueOf(apellido1Value));
            pacientesForm.jTextFieldApellido2.setText(String.valueOf(apellido2Value));
            pacientesForm.jTextFieldTelefono.setText(String.valueOf(telefonoValue));
            pacientesForm.jTextFieldDni.setText(String.valueOf(dniValue));
            pacientesForm.jTextFieldSip.setText(String.valueOf(sipValue));
        }

    }

    // Método para setear el formulario de edición
    public void setearForm() {

        // seteamos a ""   
        pacientesForm.jTextFieldID.setText(String.valueOf(""));
        pacientesForm.jTextFieldNombre.setText(String.valueOf(""));
        pacientesForm.jTextFieldApellido1.setText(String.valueOf(""));
        pacientesForm.jTextFieldApellido2.setText(String.valueOf(""));
        pacientesForm.jTextFieldTelefono.setText(String.valueOf(""));
        pacientesForm.jTextFieldDni.setText(String.valueOf(""));
        pacientesForm.jTextFieldSip.setText(String.valueOf(""));

    }

    /**
     * Método para filtrar el formulário médicos.
     *
     */
    public void filtrarPacientes(FiltroPacientes filtro) {
        Session session = sessionFactory.openSession();

        String hql = "FROM Pacientes m WHERE (:nombre IS NULL OR m.nombre = :nombre) "
                + "AND (:apellido1 IS NULL OR m.apellido1 = :apellido1 "
                + "OR m.dni = :dni)";

        // montamos un objeto Pacientes list con la
        List<Pacientes> pacientesList = session.createQuery(hql)
                .setParameter("nombre", filtro.getNombre())
                .setParameter("apellido1", filtro.getApellido1())
                .setParameter("dni", filtro.getDni()).list();

        // ejecutamos la consulta y la pasamos a lista
        //   List<Pacientes> pacientesList = query.getResultList();
        JTable table = new JTable();
        table = pacientesForm.jTablePacientes;

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Define las cabeceras de la tabla
        String[] columnNames = {"ID", "Nombre", "Apellido1", "Apellido2", "Teléfono", "DNI", "SIP"};
        model.setColumnIdentifiers(columnNames);

        for (Pacientes paciente : pacientesList) {
            Object[] row = {
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido1(),
                paciente.getApellido2(),
                paciente.getTelefono(),
                paciente.getDni(),
                paciente.getSip(),
             
            };
            model.addRow(row);
        }

        session.close();

    }
    
    
       private void setearFiltro() {
                
           pacientesForm.jTextFieldFiltroNombre.setText("");
           pacientesForm.jTextFieldFiltroApe1.setText("");
           pacientesForm.jTextFieldFiltroDni.setText("");
           
           JTable tabla = new JTable();
           
           tabla = pacientesForm.jTablePacientes;
           
           cargarPacientesEnTabla(tabla);
           
                      
            }

}
