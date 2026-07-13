package com.fitlife.fitlifespa.util;

import com.fitlife.fitlifespa.model.PlanEntrenamiento;
import com.fitlife.fitlifespa.repository.PlanEntrenamientoRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PlanEntrenamientoRepository planEntrenamientoRepository;

    private static final String[] TIPOS_PLAN = {
        "Plan Spinning", "Plan Yoga", "Plan Funcional", "Plan Crossfit",
        "Plan Pilates", "Plan Cardio", "Plan Fuerza", "Plan Movilidad"
    };

    @Override
    public void run(String... args) throws Exception {

        Faker faker = new Faker();

       
        planEntrenamientoRepository.deleteAll();

        for (int i = 0; i < 50; i++) {

            PlanEntrenamiento plan = new PlanEntrenamiento();
            plan.setNombrePlan(TIPOS_PLAN[ThreadLocalRandom.current().nextInt(TIPOS_PLAN.length)]);
            plan.setEntrenador(faker.name().fullName());
            plan.setDuracionSemanas(ThreadLocalRandom.current().nextInt(1, 16));
            plan.setActivo(faker.random().nextBoolean());
            // Asocia el plan a un usuario/socio existente en ms-socios (que también carga 50 registros de prueba)
            plan.setUsuarioId((long) ThreadLocalRandom.current().nextInt(1, 51));

            planEntrenamientoRepository.save(plan);
        }
    }
}
