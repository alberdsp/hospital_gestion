package hospital_gestion.controlador;

import hospital_gestion.modelo.Citas;
import hospital_gestion.modelo.FiltroCitas;
import hospital_gestion.modelo.Medicos;
import hospital_gestion.modelo.Pacientes;
import hospital_gestion.vista.CitasForm;
import hospital_gestion.vista.MedicosForm;
import hospital_gestion.vista.PacientesForm;
import java.awt.Dimension;
import java.awt.Toolkit;
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
                Medicos medico = new Medicos();
                Pacientes paciente = new Pacientes();
                // si los valores vacios ponemos a null
                if (idmedico.equals("")) {
                    medico = null;
                } else {
                    medico = buscarMedico(Long.parseLong(idmedico));
                }
                if (idpaciente.equals("")) {

                    paciente = null;
                } else {
                    paciente = buscarPaciente(Long.parseLong(idpaciente));
                }

                FiltroCitas filtro = new FiltroCitas();
                filtro.setPaciente(paciente);
                filtro.setMedico(medico);

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

        // Agregar un ActionListener para buscar filtro paciente
        citasForm.getjButtonBuscaPacienteFiltro().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // instanciamos formulario de paciente

                PacientesForm pacientesForm = new PacientesForm();

                // centramos el formulario al centro de la ventana
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (int) ((dimension.getWidth() - pacientesForm.getWidth()) / 2);
                int y = (int) ((dimension.getHeight() - pacientesForm.getHeight()) / 2);
                pacientesForm.setLocation(x, y);
                // cargamos el controlador
                PacientesFormController pacientesFormController = new PacientesFormController(pacientesForm);
                pacientesForm.setVisible(true);
                citasForm.add(pacientesForm);

                // Hacemos que el hijo se comporte como un diálogo modal
                JOptionPane.showMessageDialog(citasForm, pacientesForm, "Seleccione el paciente de la lista y puse aceptar", JOptionPane.PLAIN_MESSAGE);

                citasForm.getjTextFieldFiltroIDPaciente().setText(pacientesForm.getId());

            }
        });

        // Agregar un ActionListener para buscar filtro médico
        citasForm.getjButtonBuscarMedicoFiltro().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // instanciamos formulario de paciente

                MedicosForm medicosForm = new MedicosForm();

                // centramos el formulario al centro de la ventana
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (int) ((dimension.getWidth() - medicosForm.getWidth()) / 2);
                int y = (int) ((dimension.getHeight() - medicosForm.getHeight()) / 2);
                medicosForm.setLocation(x, y);
                // cargamos el controlador
                MedicosFormController pacientesFormController = new MedicosFormController(medicosForm);
                medicosForm.setVisible(true);
                citasForm.add(medicosForm);

                // Hacemos que el hijo se comporte como un diálogo modal
                JOptionPane.showMessageDialog(citasForm, medicosForm, "Seleccione el paciente de la lista y puse aceptar", JOptionPane.PLAIN_MESSAGE);

                citasForm.getjTextFieldFiltroIDMedico().setText(medicosForm.getId());

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
     * Método para guardar en la base de datos o actualizar cita
     *
     * @param cita recibe un objeto cita
     * @return devuelve tru o false si se guarda con éxito
     */
    public boolean guardarCita(Citas cita) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        // si viene con id actualizamos sino creamos nueva
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
        String[] columnNames = {"ID", "Fecha", "ID Paciente", "Paciente", "ID Medico", "Medico"};
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
                cita.getMedicos(), //      cita.getMedicos().getNombre(),
            };
            model.addRow(row);
        }

        session.close();
    }

    // Método para obtener los datos del formulario y crear un objeto Citas
    private Citas obtenerDatosDelFormulario() {

        // Crear un objeto Citas con los datos obtenidos
        Citas cita = new Citas();
        // Obtener los valores de los campos de texto
        String id = citasForm.getjTextFieldID().getText();
        String idpaciente = citasForm.getjTextFieldIDPaciente().getText();
        String idmedico = citasForm.getjTextFieldIDMedico().getText();
        Date fecha = (Date) citasForm.getjSpinnerFechaCita().getValue();

        cita.setFecha(fecha); // Maneja la excepción si la cadena no puede ser parseada
        cita.setPacientes(buscarPaciente(Long.parseLong(idpaciente)));
        cita.setMedicos(buscarMedico(Long.parseLong(idmedico)));

        if (id.equals("")) {

        } else {
            cita.setId(Long.parseLong(id));
        }

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
        citasForm.getjTextFieldIDPaciente().setEnabled(true);
        citasForm.getjTextFieldIDMedico().setText(String.valueOf(""));
        citasForm.getjTextFieldIDMedico().setEnabled(true);
        citasForm.getjTextFieldNombreMedico().setText(null);
        citasForm.getjTextFieldNombrePaciente().setText(null);
    }

    /**
     * Método para filtrar citas.
     *
     */
    public void filtrarCitas(FiltroCitas filtro) {
        Session session = sessionFactory.openSession();

        String hql = "FROM Citas m WHERE (:pacientes IS NULL OR m.pacientes = :pacientes) "
                + "AND (:medicos IS NULL OR m.medicos = :medicos)";

        // montamos un objeto Citas list con la
        List<Citas> citasList = session.createQuery(hql)
                .setParameter("pacientes", filtro.getPaciente())
                .setParameter("medicos", filtro.getMedico()).list();

        // ejecutamos la consulta y la pasamos a lista
        //   List<Citas> citasList = query.getResultList();
        JTable table = new JTable();
        table = citasForm.getjTableCitas();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        // Define las cabeceras de la tabla
        String[] columnNames = {"ID", "Fecha", "ID Paciente", "Paciente", "ID Medico", "Medico"};
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
                cita.getMedicos(), //      cita.getMedicos().getNombre(),
            };
            model.addRow(row);
        }

        session.close();
    }

    // médodo para setear el filtro en blanco
    private void setearFiltro() {

        citasForm.getjTextFieldFiltroIDPaciente().setText("");
        citasForm.getjTextFieldFiltroIDMedico().setText("");

        JTable tabla = new JTable();

        tabla = citasForm.getjTableCitas();

        cargarCitasEnTabla(tabla);

    }

    /**
     * Método para buscar un paciente por id
     *
     * @param idpaciente String con id
     * @return Pacientes objeto
     */
    public Pacientes buscarPaciente(Long id) {
        Session session = sessionFactory.openSession();

        String hql = "FROM Pacientes m WHERE (m.id = :id)";

        // montamos un objeto Medico filtrado
        List<Pacientes> pacienteList = session.createQuery(hql)
                .setParameter("id", id).list();

        Pacientes paciente = pacienteList.get(0);
        session.close();

        return paciente;
    }

    /**
     * Método para buscar un médico por id
     *
     * @param idmedico String con id
     * @return Medcicos objeto
     */
    public Medicos buscarMedico(Long id) {
        Session session = sessionFactory.openSession();

        String hql = "FROM Medicos m WHERE (m.id = :id)";

        // montamos un objeto Medico filtrado
        List<Medicos> medicosList = session.createQuery(hql)
                .setParameter("id", id).list();

        Medicos medico = medicosList.get(0);

        session.close();

        return medico;
    }

}
