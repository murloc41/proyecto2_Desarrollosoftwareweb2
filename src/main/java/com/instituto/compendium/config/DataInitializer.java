package com.instituto.compendium.config;

import com.instituto.compendium.model.Role;
import com.instituto.compendium.model.Usuario;
import com.instituto.compendium.model.Juego;
import com.instituto.compendium.model.Guia;
import com.instituto.compendium.model.Paciente;
import com.instituto.compendium.model.Medicamento;
import com.instituto.compendium.repository.RoleRepository;
import com.instituto.compendium.repository.UsuarioRepository;
import com.instituto.compendium.repository.JuegoRepository;
import com.instituto.compendium.repository.GuiaRepository;
import com.instituto.compendium.repository.PacienteRepository;
import com.instituto.compendium.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JuegoRepository juegoRepository;

    @Autowired
    private GuiaRepository guiaRepository;

    @Autowired
    private com.instituto.compendium.repository.CategoriaRepository categoriaRepository;

    @Autowired
    private com.instituto.compendium.repository.PlataformaRepository plataformaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Inicializando datos del sistema ===");
        
        // 1. Crear plataformas
        plataformaRepository.findByNombre("PC").orElseGet(() -> {
            com.instituto.compendium.model.Plataforma p = new com.instituto.compendium.model.Plataforma();
            p.setNombre("PC");
            p.setDescripcion("Computadora personal");
            System.out.println("✓ Plataforma PC creada");
            return plataformaRepository.save(p);
        });

        plataformaRepository.findByNombre("PS5").orElseGet(() -> {
            com.instituto.compendium.model.Plataforma p = new com.instituto.compendium.model.Plataforma();
            p.setNombre("PS5");
            p.setDescripcion("PlayStation 5");
            System.out.println("✓ Plataforma PS5 creada");
            return plataformaRepository.save(p);
        });

        plataformaRepository.findByNombre("PS4").orElseGet(() -> {
            com.instituto.compendium.model.Plataforma p = new com.instituto.compendium.model.Plataforma();
            p.setNombre("PS4");
            p.setDescripcion("PlayStation 4");
            System.out.println("✓ Plataforma PS4 creada");
            return plataformaRepository.save(p);
        });

        plataformaRepository.findByNombre("Xbox Series X|S").orElseGet(() -> {
            com.instituto.compendium.model.Plataforma p = new com.instituto.compendium.model.Plataforma();
            p.setNombre("Xbox Series X|S");
            p.setDescripcion("Xbox Series X y S");
            System.out.println("✓ Plataforma Xbox Series creada");
            return plataformaRepository.save(p);
        });

        plataformaRepository.findByNombre("Nintendo Switch").orElseGet(() -> {
            com.instituto.compendium.model.Plataforma p = new com.instituto.compendium.model.Plataforma();
            p.setNombre("Nintendo Switch");
            p.setDescripcion("Nintendo Switch");
            System.out.println("✓ Plataforma Switch creada");
            return plataformaRepository.save(p);
        });

        // 2. Crear categorías de juegos
        com.instituto.compendium.model.Categoria catAventura = categoriaRepository.findByNombre("Aventura").orElseGet(() -> {
            com.instituto.compendium.model.Categoria c = new com.instituto.compendium.model.Categoria();
            c.setNombre("Aventura");
            c.setDescripcion("Juegos de aventura y exploración");
            System.out.println("✓ Categoría Aventura creada");
            return categoriaRepository.save(c);
        });

        com.instituto.compendium.model.Categoria catRPG = categoriaRepository.findByNombre("RPG").orElseGet(() -> {
            com.instituto.compendium.model.Categoria c = new com.instituto.compendium.model.Categoria();
            c.setNombre("RPG");
            c.setDescripcion("Juegos de rol y acción");
            System.out.println("✓ Categoría RPG creada");
            return categoriaRepository.save(c);
        });

        com.instituto.compendium.model.Categoria catSandbox = categoriaRepository.findByNombre("Sandbox").orElseGet(() -> {
            com.instituto.compendium.model.Categoria c = new com.instituto.compendium.model.Categoria();
            c.setNombre("Sandbox");
            c.setDescripcion("Juegos de mundo abierto y construcción");
            System.out.println("✓ Categoría Sandbox creada");
            return categoriaRepository.save(c);
        });

        com.instituto.compendium.model.Categoria catSimulacion = categoriaRepository.findByNombre("Simulación").orElseGet(() -> {
            com.instituto.compendium.model.Categoria c = new com.instituto.compendium.model.Categoria();
            c.setNombre("Simulación");
            c.setDescripcion("Juegos de simulación y gestión");
            System.out.println("✓ Categoría Simulación creada");
            return categoriaRepository.save(c);
        });

        categoriaRepository.findByNombre("Acción").orElseGet(() -> {
            com.instituto.compendium.model.Categoria c = new com.instituto.compendium.model.Categoria();
            c.setNombre("Acción");
            c.setDescripcion("Juegos de acción y combate");
            System.out.println("✓ Categoría Acción creada");
            return categoriaRepository.save(c);
        });

        // 2. Crear roles
        Role admin = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role r = new Role(); 
            r.setName("ADMIN"); 
            System.out.println("✓ Rol ADMIN creado");
            return roleRepository.save(r);
        });

        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role r = new Role(); 
            r.setName("USER"); 
            System.out.println("✓ Rol USER creado");
            return roleRepository.save(r);
        });

        Role autorRole = roleRepository.findByName("AUTOR").orElseGet(() -> {
            Role r = new Role();
            r.setName("AUTOR");
            System.out.println("✓ Rol AUTOR creado");
            return roleRepository.save(r);
        });

        // 2. Crear usuarios de ejemplo
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario u = new Usuario();
            u.setUsername("admin");
            u.setEmail("admin@example.com");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setActivo(true);
            u.getRoles().add(admin);
            usuarioRepository.save(u);
            System.out.println("✓ Usuario ADMIN creado (username: admin, password: admin123)");
        }

        // Crear usuario autor de ejemplo
        if (usuarioRepository.findByUsername("autor1").isEmpty()) {
            Usuario autor = new Usuario();
            autor.setUsername("autor1");
            autor.setEmail("autor1@example.com");
            autor.setPassword(passwordEncoder.encode("autor123"));
            autor.setActivo(true);
            autor.getRoles().add(autorRole);
            autor.getRoles().add(userRole);
            usuarioRepository.save(autor);
            System.out.println("✓ Usuario AUTOR creado (username: autor1, password: autor123)");
        }

        // Crear usuario común de ejemplo
        if (usuarioRepository.findByUsername("usuario1").isEmpty()) {
            Usuario user = new Usuario();
            user.setUsername("usuario1");
            user.setEmail("usuario1@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setActivo(true);
            user.getRoles().add(userRole);
            usuarioRepository.save(user);
            System.out.println("✓ Usuario común creado (username: usuario1, password: user123)");
        }

        // 3. Crear juegos de ejemplo (solo si no existen)
        if (juegoRepository.count() == 0) {
            Juego juego1 = new Juego();
            juego1.setTitulo("The Legend of Zelda: Breath of the Wild");
            juego1.setDescripcion("Explora un vasto mundo abierto lleno de aventuras y misterios.");
            juego1.getCategorias().add(catAventura);
            plataformaRepository.findByNombre("Nintendo Switch").ifPresent(juego1.getPlataformas()::add);
            juego1.setImagen("/images/zelda-totk.jpg");
            juego1.setCalificacion(4.8);
            juego1.setTotalValoraciones(1250);
            juegoRepository.save(juego1);

            Juego juego2 = new Juego();
            juego2.setTitulo("Elden Ring");
            juego2.setDescripcion("Un juego de rol de acción en un mundo oscuro y fantástico.");
            juego2.getCategorias().add(catRPG);
            plataformaRepository.findByNombre("PC").ifPresent(juego2.getPlataformas()::add);
            plataformaRepository.findByNombre("PS5").ifPresent(juego2.getPlataformas()::add);
            plataformaRepository.findByNombre("Xbox Series X|S").ifPresent(juego2.getPlataformas()::add);
            juego2.setImagen("/images/elden-ring.jpg");
            juego2.setCalificacion(4.7);
            juego2.setTotalValoraciones(2100);
            juegoRepository.save(juego2);

            Juego juego3 = new Juego();
            juego3.setTitulo("Minecraft");
            juego3.setDescripcion("Construye, explora y sobrevive en un mundo de bloques infinito.");
            juego3.getCategorias().add(catSandbox);
            plataformaRepository.findByNombre("PC").ifPresent(juego3.getPlataformas()::add);
            plataformaRepository.findByNombre("PS5").ifPresent(juego3.getPlataformas()::add);
            plataformaRepository.findByNombre("PS4").ifPresent(juego3.getPlataformas()::add);
            plataformaRepository.findByNombre("Nintendo Switch").ifPresent(juego3.getPlataformas()::add);
            juego3.setImagen("/images/minecraft.jpg");
            juego3.setCalificacion(4.6);
            juego3.setTotalValoraciones(5000);
            juegoRepository.save(juego3);

            Juego juego4 = new Juego();
            juego4.setTitulo("Dark Souls III");
            juego4.setDescripcion("Un desafiante RPG de acción con combates tácticos.");
            juego4.getCategorias().add(catRPG);
            plataformaRepository.findByNombre("PC").ifPresent(juego4.getPlataformas()::add);
            plataformaRepository.findByNombre("PS4").ifPresent(juego4.getPlataformas()::add);
            juego4.setImagen("/images/dark-souls.jpg");
            juego4.setCalificacion(4.5);
            juego4.setTotalValoraciones(1800);
            juegoRepository.save(juego4);

            Juego juego5 = new Juego();
            juego5.setTitulo("Stardew Valley");
            juego5.setDescripcion("Administra tu granja y forma parte de una comunidad vibrante.");
            juego5.getCategorias().add(catSimulacion);
            plataformaRepository.findByNombre("PC").ifPresent(juego5.getPlataformas()::add);
            plataformaRepository.findByNombre("PS5").ifPresent(juego5.getPlataformas()::add);
            plataformaRepository.findByNombre("Nintendo Switch").ifPresent(juego5.getPlataformas()::add);
            juego5.setImagen("/images/stardew-valley.jpg");
            juego5.setCalificacion(4.9);
            juego5.setTotalValoraciones(3500);
            juegoRepository.save(juego5);

            System.out.println("✓ 5 Juegos de ejemplo creados");
        }

        // 3.1 Asegurar categorías base ampliadas (solo creación si faltan)
        String[] categoriasExtra = {"Estrategia","Deportes","Puzzle","Plataformas","Shooter","Lucha","Survival","MMORPG"};
        for (String nombreCat : categoriasExtra) {
            categoriaRepository.findByNombre(nombreCat).orElseGet(() -> {
                com.instituto.compendium.model.Categoria c = new com.instituto.compendium.model.Categoria();
                c.setNombre(nombreCat);
                c.setDescripcion("Categoría adicional: " + nombreCat);
                System.out.println("✓ Categoría adicional creada: " + nombreCat);
                return categoriaRepository.save(c);
            });
        }

        // 4. Crear guías de ejemplo
        if (guiaRepository.count() == 0) {
            Usuario autor = usuarioRepository.findByUsername("autor1").orElse(null);
            if (autor != null && juegoRepository.count() > 0) {
                Juego primerJuego = juegoRepository.findAll().get(0);
                
                Guia guia1 = new Guia();
                guia1.setTitulo("Guía completa para principiantes");
                guia1.setDescripcion("Aprende los conceptos básicos y comienza tu aventura con éxito.");
                guia1.setContenido("Esta guía te ayudará a entender los controles básicos, la mecánica del juego y los primeros pasos que debes seguir...");
                guia1.setJuego(primerJuego);
                guia1.setAutor(autor);
                guia1.setDificultad(Guia.Dificultad.PRINCIPIANTE);
                guia1.setCategoria(Guia.Categoria.TUTORIAL);
                guia1.setEstado(Guia.EstadoPublicacion.PUBLICADO);
                guia1.getTags().add("principiante");
                guia1.getTags().add("tutorial");
                guia1.setVistas(150);
                guia1.setRating(4.5);
                guia1.setTotalValoraciones(30);
                guiaRepository.save(guia1);

                Guia guia2 = new Guia();
                guia2.setTitulo("Mejores builds para PvP");
                guia2.setDescripcion("Descubre las construcciones de personajes más efectivas para combate jugador vs jugador.");
                guia2.setContenido("En esta guía analizamos las mejores combinaciones de estadísticas, armas y habilidades para dominar en PvP...");
                guia2.setJuego(primerJuego);
                guia2.setAutor(autor);
                guia2.setDificultad(Guia.Dificultad.AVANZADO);
                guia2.setCategoria(Guia.Categoria.BUILD);
                guia2.setEstado(Guia.EstadoPublicacion.PUBLICADO);
                guia2.getTags().add("pvp");
                guia2.getTags().add("build");
                guia2.getTags().add("avanzado");
                guia2.setVistas(320);
                guia2.setRating(4.8);
                guia2.setTotalValoraciones(55);
                guiaRepository.save(guia2);

                System.out.println("✓ Guías de ejemplo creadas");
            }
        }
        
        // Sembrar datos para Pacientes
        System.out.println("\n--- Inicializando Pacientes ---");
        Paciente p1 = new Paciente();
        p1.setNombre("Juan Pérez");
        p1.setRut("12.345.678-9");
        p1.setPiso(2);
        p1.setTurno("Mañana");
        pacienteRepository.save(p1);
        System.out.println("✓ Paciente: Juan Pérez");

        Paciente p2 = new Paciente();
        p2.setNombre("María García");
        p2.setRut("98.765.432-1");
        p2.setPiso(4);
        p2.setTurno("Tarde");
        pacienteRepository.save(p2);
        System.out.println("✓ Paciente: María García");

        Paciente p3 = new Paciente();
        p3.setNombre("Carlos López");
        p3.setRut("55.555.555-5");
        p3.setPiso(1);
        p3.setTurno("Noche");
        pacienteRepository.save(p3);
        System.out.println("✓ Paciente: Carlos López");

        // Sembrar datos para Medicamentos
        System.out.println("\n--- Inicializando Medicamentos ---");
        Medicamento m1 = new Medicamento("Amoxicilina", 500, "Antibiótico", false);
        medicamentoRepository.save(m1);
        System.out.println("✓ Medicamento: Amoxicilina");

        Medicamento m2 = new Medicamento("Metformina", 850, "Antidiabético", false);
        medicamentoRepository.save(m2);
        System.out.println("✓ Medicamento: Metformina");

        Medicamento m3 = new Medicamento("Warfarina", 5, "Anticoagulante", true);
        medicamentoRepository.save(m3);
        System.out.println("✓ Medicamento: Warfarina (uso delicado)");
        
        System.out.println("\n=== Inicialización completada ===");
        System.out.println("Accede a H2 Console en: http://localhost:8080/h2-console");
        System.out.println("JDBC URL: jdbc:h2:file:./data/compendium");
        System.out.println("Usuario: sa | Password: password");
        System.out.println("=====================================\n");
    }
}
