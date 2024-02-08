package hospital_gestion.controlador;

import hospital_gestion.modelo.Citas;
import hospital_gestion.modelo.FiltroCitas;
import hospital_gestion.modelo.Medicos;
import hospital_gestion.modelo.Pacientes;
import hospital_gestion.vista.CitasForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Clase que controla el formulario de Citas
 *
 * @author Alber 2024
 */
public class CitasFormController {

    private SessionFactory sessionFactory;
    private final CitasForm citasForm;

    public CitasFormController(CitasForm citasForm) {
        this.citasForm = citasForm;

        // Inicializa la sesión de Hibernate
        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(Citas.class)
                .buildSessionFactory();

        cargarCitasEnTabla(citasForm.getjTableCitas());

        configurarListeners();
    }

    // aquí configuramos todos los listeners de la aplicación
    private void configurarListeners() {

        // Agregar un ActionListener para el botón "Grabar"
        citasForm.getjButtonGrabar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtenemos los datos y grabamos
                Citas cita = obtenerDatosDelFormulario();
                boolean exito = guardarCita(cita);

                // comprobamos si se grabó
                if (exito) {
                    // Éxito:
                } else {
                    // Error: 
                }
            }
        });

        // Agregar un ActionListener para el botón "Filtrar"
        citasForm.getjButtonFiltro().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // armamos los filtros
                String idpaciente = citasForm.getjTextFieldFiltroIDPaciente().getText();
                String idmedico = citasForm.getjTextFieldFiltroIDMedico().getText();
                Date filtrofecha = (Date) citasForm.getjSpinnerFechaFiltro().getValue();

                // Reemplazamos las cadenas vacías con null
                idpaciente = (idpaciente.isEmpty()) ? null : idpaciente;
                idmedico = (idmedico.isEmpty()) ? null : idpaciente;
                filtrofecha = (filtrofecha.equals(null)) ? null : filtrofecha;

                FiltroCitas filtro = new FiltroCitas();
                filtro.setIdpaciente(idpaciente);
                filtro.setIdmedico(idmedico);
                filtro.setFechaCita(filtrofecha);

                // filtramos pasando el objeto con los filtros
                filtrarCitas(filtro);
            }
        });

        // Agregar un ActionListener para el botón "Reset"
        citasForm.getjButtonReset().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setearFiltro();
            }

        });

        // Agregar un ActionListener para el botón "Nuevo"
        citasForm.getjButtonNuevo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setearForm();
            }
        });

        // Agregar un ActionListener para el botón "Borrar"
        citasForm.getjButtonEliminar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtenemos los datos y borramos
                Citas cita = obtenerDatosDelFormulario();
                boolean exito = eliminarMedico(cita);

                // comprobamos si se borró
                if (exito) {
                    // Éxito:
                } else {
                    // Error: 
                }
            }
        });

        // Agregar ActionListener a la tabla para detectar la selección de fila
        citasForm.getjTableCitas().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Llamar al método para cargar datos cuando se selecciona una fila
                cargarFilaSelect();
            }
        });

    }

    /**
     * Método para guardar en la base de datos o actualizar
     *
     * @param cita recibe un objeto médico
     * @return devuelve tru o false si se guarda con éxito
     */
    public boolean guardarCita(Citas cita) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // si no tiene id entendemos que es nuevo
            if (cita.getId() == null) {
                session.save(cita);
            } else {
                // si tiene hacemos merge para update
                session.merge(cita);

            }

            transaction.commit();

            cargarCitasEnTabla(citasForm.getjTableCitas());

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
    public boolean eliminarMedico(Citas cita) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            if (cita.getId() == null) {
                // no hacemos nada si es nulo
            } else {

                session.delete(cita);
            }

            transaction.commit();

            cargarCitasEnTabla(citasForm.getjTableCitas());

            JOptionPane.showMessageDialog(null, "Cita borrada corréctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                System.out.println(e);
            }
            e.printStackTrace();
            // Mostrar un mensaje de alerta
            JOptionPane.showMessageDialog(null, "No se puede borrar el cita,", "Alerta", JOptionPane.WARNING_MESSAGE);
            return false;
        } finally {
            session.close();
        }
    }

    // Método para cargar la lista de médicos en una tabla
    public void cargarCitasEnTabla(JTable table) {
        Session session = sessionFactory.openSession();
        //     List<Citas> citasList = session.createQuery("from Citas").list();
        List<Citas> citasList = session.createQuery("from Citas").setMaxResults(50).list();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Define las cabeceras de la tabla
        String[] columnNames = {"ID", "Fecha", "ID Paciente", "Paciente","ID Medico", "Medico"};
        model.setColumnIdentifiers(columnNames);

        for (Citas cita : citasList) {

            // Obtenemos la fecha de la cita
            Date fecha = cita.getFecha();

            // Creamos un objeto SimpleDateFormat para formatear la fecha 
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String fechasalida = outputFormat.format(fecha);

            Object[] row = {
                cita.getId(),
                fechasalida,
                cita.getPacientes().getId(),
                cita.getPacientes().getNombre() + " " + cita.getPacientes().getApellido1(),
                cita.getMedicos().getId(),
                cita.getMedicos().getNombre() + " " + cita.getMedicos().getApellido1(), 
                cita.getPacientes(),
                cita.getMedicos(),
            //      cita.getMedicos().getNombre(),
            };
            model.addRow(row);
        }

        session.close();
    }

    // Método para obtener los datos del formulario y crear un objeto Citas
    private Citas obtenerDatosDelFormulario() {
        // Obtener los valores de los campos de texto

        // Define el formato de fecha y hora
        String id = citasForm.getjTextFieldID().getText();
        Medicos medico = citasForm.getMedico();
        Pacientes paciente = citasForm.getPaciente();

        try {
            // Obtenemos el valor del Spinner como un objeto
            Object valorSpinner = citasForm.getjSpinnerFechaCita().getValue();

            // Convierte el valor del Spinner a String
            String fechaHoraString = valorSpinner.toString();

            // Define el formato de fecha y hora
            SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            // Parsea la cadena de fecha y hora a un objeto Date
            Date fechaHora = formatoFechaHora.parse(fechaHoraString);

           
          
        } catch (ParseException e) {
            // Maneja la excepción si la cadena no puede ser parseada
            e.printStackTrace();
        }

        // Crear un objeto Citas con los datos obtenidos
        Citas cita = new Citas();

        if (id.equals("")) {

        } else {
            cita.setId(Long.parseLong(id));
        }

        
        cita.setMedicos(medico);
        cita.setPacientes(paciente);

        return cita;
    }

    // Método para cargar los datos de la fila seleccionada en los campos de edición
    public void cargarFilaSelect() {
 int selectedRow = citasForm.getjTableCitas().getSelectedRow();

    // Verifica si se ha seleccionado una fila
    if (selectedRow != -1) {
        DefaultTableModel model = (DefaultTableModel) citasForm.getjTableCitas().getModel();

        // Obtiene los valores de las columnas de la fila seleccionada
        Object id = model.getValueAt(selectedRow, 0);
        Object fechacita = model.getValueAt(selectedRow, 1);
         Object idpaciente = model.getValueAt(selectedRow, 2);
        Object nomPaciente = model.getValueAt(selectedRow, 3);   
        Object idmedico = model.getValueAt(selectedRow, 4);
        Object nomMedico = model.getValueAt(selectedRow, 5);
        // Establece los valores en los campos de edición
        citasForm.getjTextFieldID().setText(id.toString());
        citasForm.getjTextFieldIDPaciente().setText(idpaciente.toString());
        citasForm.getjTextFieldNombrePaciente().setText(nomPaciente.toString());
         citasForm.getjTextFieldIDMedico().setText(idmedico.toString());
        citasForm.getjTextFieldNombreMedico().setText(nomMedico.toString());

        // Manejo de la fecha de cita
        try {
            // Convierte el valor de la fecha de cita a un formato adecuado
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date fechaCita = formatoFecha.parse(fechacita.toString());

            // Configura el valor del JSpinner con la fecha de cita convertida
            citasForm.getjSpinnerFechaCita().setValue(fechaCita);
        } catch (ParseException e) {
            // Maneja la excepción si la fecha no se puede parsear
            e.printStackTrace();
        }
    }

    }

    // Método para setear el formulario de edición
    public void setearForm() {

        // seteamos a ""   
        citasForm.getjTextFieldID().setText(String.valueOf(""));
        citasForm.getjTextFieldIDPaciente().setText(String.valueOf(""));
        citasForm.getjTextFieldIDMedico().setText(String.valueOf(""));
        citasForm.getjTextFieldNombreMedico().setText(null);

    }

    /**
     * Método para filtrar el formulário médicos.
     *
     */
    public void filtrarCitas(FiltroCitas filtro) {
        Session session = sessionFactory.openSession();

        String hql = "FROM Citas m WHERE (:fecha IS NULL OR m.fecha = :fecha) "
                + "AND (:idpaciente IS NULL OR m.idpaciente = :idpaciente "
                + "OR m.idmedico = :idmedico)";

        // montamos un objeto Citas list con la
        List<Citas> citasList = session.createQuery(hql)
                .setParameter("fecha", filtro.getFechaCita())
                .setParameter("idpaciente", filtro.getIdpaciente())
                .setParameter("idmedico", filtro.getIdmedico()).list();

        // ejecutamos la consulta y la pasamos a lista
        //   List<Citas> citasList = query.getResultList();
        JTable table = new JTable();
        table = citasForm.getjTableCitas();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Define las cabeceras de la tabla
        String[] columnNames = {"ID", "Fecha", "Paciente", "Medico"};
        model.setColumnIdentifiers(columnNames);

        for (Citas cita : citasList) {
            Object[] row = {
                cita.getId(),
                cita.getFecha(),
                cita.getPacientes().getNombre() + " " + cita.getPacientes().getApellido1(),
                cita.getMedicos().getNombre() + " " + cita.getMedicos().getApellido1(),};
            model.addRow(row);
        }

        session.close();
    }

    private void setearFiltro() {

        citasForm.getjTextFieldFiltroIDPaciente().setText("");
        citasForm.getjTextFieldFiltroIDMedico().setText("");
        citasForm.getjSpinnerFechaFiltro().setValue(null);
        JTable tabla = new JTable();

        tabla = citasForm.getjTableCitas();

        cargarCitasEnTabla(tabla);

    }


}
