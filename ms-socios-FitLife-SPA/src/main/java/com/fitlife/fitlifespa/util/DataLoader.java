package com.fitlife.fitlifespa.util;

import com.fitlife.fitlifespa.model.Usuario;
import com.fitlife.fitlifespa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        Faker faker = new Faker();

        
        usuarioRepository.deleteAll();

        for (int i = 0; i < 50; i++) {

            Usuario usuario = new Usuario();
            usuario.setNombre(faker.name().fullName());
            usuario.setEmail(faker.internet().emailAddress());
            usuario.setTelefono(faker.phoneNumber().phoneNumber());
            usuario.setActivo(faker.random().nextBoolean());
            usuario.setPassword(passwordEncoder.encode("password123"));

            usuarioRepository.save(usuario);
        }
    }
}
