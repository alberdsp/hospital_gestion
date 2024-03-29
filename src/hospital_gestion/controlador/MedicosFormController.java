package hospital_gestion.controlador;

import hospital_gestion.modelo.FiltroMedicos;
import hospital_gestion.modelo.Medicos;
import hospital_gestion.vista.MedicosForm;
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

public class MedicosFormController {

    private SessionFactory sessionFactory;
    private final MedicosForm medicosForm;

    public MedicosFormController(MedicosForm medicosForm) {
        this.medicosForm = medicosForm;

        // Inicializa la sesión de Hibernate
        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(Medicos.class)
                .buildSessionFactory();

        cargarMedicosEnTabla(medicosForm.getjTableMedicos());

        configurarListeners();
    }

    // aquí configuramos todos los listeners de la aplicación
    private void configurarListeners() {

        // Agregar un ActionListener para el botón "Grabar"
        medicosForm.getjButtonGrabar().addActionListener(new ActionListener() {
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

        // Agregar un ActionListener para el botón "Filtrar"
        medicosForm.getjButtonFiltro().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO   armar bien el filtro para pasar objeto con sus parámtros
                // armamos los filtros
                String nombre = medicosForm.getjTextFieldFiltroNombre().getText();
                String apellido1 = medicosForm.getjTextFieldFiltroApe1().getText();
                String numeroColegiado = medicosForm.getjTextFieldFiltroNcolegiado().getText();

                // Reemplazamos las cadenas vacías con null
                nombre = (nombre.isEmpty()) ? null : nombre;
                apellido1 = (apellido1.isEmpty()) ? null : apellido1;
                numeroColegiado = (numeroColegiado.isEmpty()) ? null : numeroColegiado;

                FiltroMedicos filtro = new FiltroMedicos();
                filtro.setNombre(nombre);
                filtro.setApellido1(apellido1);
                filtro.setNumeroColegiado(numeroColegiado);

                // filtramos pasando el objeto con los filtros
                filtrarMedicos(filtro);
            }
        });

        // Agregar un ActionListener para el botón "Reset"
        medicosForm.getjButtonReset().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setearFiltro();
            }

        });

        // Agregar un ActionListener para el botón "Nuevo"
        medicosForm.getjButtonNuevo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setearForm();
            }
        });

        // Agregar un ActionListener para el botón "Borrar"
        medicosForm.getjButtonEliminar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtenemos los datos y borramos
                Medicos medico = obtenerDatosDelFormulario();
                boolean exito = eliminarMedico(medico);

                // comprobamos si se borró
                if (exito) {
                    // Éxito:
                } else {
                    // Error: 
                }
            }
        });

        // Agregar ActionListener a la tabla para detectar la selección de fila
        medicosForm.getjTableMedicos().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Llamar al método para cargar datos cuando se selecciona una fila
                cargarFilaSelect();
            }
        });

    }

    /**
     * Método para guardar en la base de datos o actualizar
     *
     * @param medico recibe un objeto médico
     * @return devuelve tru o false si se guarda con éxito
     */
    public boolean guardarMedico(Medicos medico) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // si no tiene id entendemos que es nuevo
            if (medico.getId() == null) {
                session.save(medico);
            } else {
                // si tiene hacemos merge para update
                session.merge(medico);

            }

            transaction.commit();

            cargarMedicosEnTabla(medicosForm.getjTableMedicos());

            JOptionPane.showMessageDialog(null, "Médico grabado corréctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
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
    public boolean eliminarMedico(Medicos medico) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            if (medico.getId() == null) {
                // no hacemos nada si es nulo
            } else {

                session.delete(medico);
            }

            transaction.commit();

            cargarMedicosEnTabla(medicosForm.getjTableMedicos());

            JOptionPane.showMessageDialog(null, "Médico borrado corréctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                System.out.println(e);
            }
            e.printStackTrace();
            // Mostrar un mensaje de alerta
            JOptionPane.showMessageDialog(null, "No se puede borrar el médico, tiene citas asociadas", "Alerta", JOptionPane.WARNING_MESSAGE);
            return false;
        } finally {
            session.close();
        }
    }

    // Método para cargar la lista de médicos en una tabla
    public void cargarMedicosEnTabla(JTable table) {
        Session session = sessionFactory.openSession();
        List<Medicos> medicosList = session.createQuery("from Medicos").setMaxResults(50).list();
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

        String id = medicosForm.getjTextFieldID().getText();
        String nombre = medicosForm.getjTextFieldNombre().getText();
        String apellido1 = medicosForm.getjTextFieldApellido1().getText();
        String apellido2 = medicosForm.getjTextFieldApellido2().getText();
        String telefono = medicosForm.getjTextFieldTelefono().getText();
        String dni = medicosForm.getjTextFieldDni().getText();
        String numeroColegiado = medicosForm.getjTextFieldNcolegiado().getText();

        // Crear un objeto Medicos con los datos obtenidos
        Medicos medico = new Medicos();

        if (id.equals("")) {

        } else {
            medico.setId(Long.parseLong(id));
        }
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

        int selectedRow = medicosForm.getjTableMedicos().getSelectedRow();

        // Verifica si se ha seleccionado una fila
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) medicosForm.getjTableMedicos().getModel();

            // Obtiene los valores de las columnas de la fila seleccionada
            Object idValue = model.getValueAt(selectedRow, 0);
            Object nombreValue = model.getValueAt(selectedRow, 1);
            Object apellido1Value = model.getValueAt(selectedRow, 2);
            Object apellido2Value = model.getValueAt(selectedRow, 3);
            Object telefonoValue = model.getValueAt(selectedRow, 4);
            Object dniValue = model.getValueAt(selectedRow, 5);
            Object ncolegiadoValue = model.getValueAt(selectedRow, 6);

            // Establece los valores en los campos de edición
            medicosForm.getjTextFieldID().setText(String.valueOf(idValue));
            medicosForm.getjTextFieldNombre().setText(String.valueOf(nombreValue));
            medicosForm.getjTextFieldApellido1().setText(String.valueOf(apellido1Value));
            medicosForm.getjTextFieldApellido2().setText(String.valueOf(apellido2Value));
            medicosForm.getjTextFieldTelefono().setText(String.valueOf(telefonoValue));
            medicosForm.getjTextFieldDni().setText(String.valueOf(dniValue));
            medicosForm.getjTextFieldNcolegiado().setText(String.valueOf(ncolegiadoValue));
            medicosForm.setId(String.valueOf(idValue));

        }

    }

    // Método para setear el formulario de edición
    public void setearForm() {

        // seteamos a ""   
        medicosForm.getjTextFieldID().setText(String.valueOf(""));
        medicosForm.getjTextFieldNombre().setText(String.valueOf(""));
        medicosForm.getjTextFieldApellido1().setText(String.valueOf(""));
        medicosForm.getjTextFieldApellido2().setText(String.valueOf(""));
        medicosForm.getjTextFieldTelefono().setText(String.valueOf(""));
        medicosForm.getjTextFieldDni().setText(String.valueOf(""));
        medicosForm.getjTextFieldNcolegiado().setText(String.valueOf(""));

    }

    /**
     * Método para filtrar el formulário médicos.
     *
     */
    public void filtrarMedicos(FiltroMedicos filtro) {
        Session session = sessionFactory.openSession();

        String hql = "FROM Medicos m WHERE (:nombre IS NULL OR m.nombre = :nombre) "
                + "AND (:apellido1 IS NULL OR m.apellido1 = :apellido1) "
                + "AND (:numeroColegiado IS NULL OR m.numeroColegiado = :numeroColegiado)";

        // montamos un objeto Medicos list con la
        List<Medicos> medicosList = session.createQuery(hql)
                .setParameter("nombre", filtro.getNombre())
                .setParameter("apellido1", filtro.getApellido1())
                .setParameter("numeroColegiado", filtro.getNumeroColegiado()).list();

        // ejecutamos la consulta y la pasamos a lista
        //   List<Medicos> medicosList = query.getResultList();
        JTable table = new JTable();
        table = medicosForm.getjTableMedicos();

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

    private void setearFiltro() {

        medicosForm.getjTextFieldFiltroNombre().setText("");
        medicosForm.getjTextFieldFiltroApe1().setText("");
        medicosForm.getjTextFieldFiltroNcolegiado().setText("");

        JTable tabla = new JTable();

        tabla = medicosForm.getjTableMedicos();

        cargarMedicosEnTabla(tabla);

    }

}
