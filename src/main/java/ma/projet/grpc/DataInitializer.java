package ma.projet.grpc;

import ma.projet.grpc.entities.Compte;
import ma.projet.grpc.repositories.CompteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CompteRepository compteRepository;

    public DataInitializer(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create sample accounts
        Compte compte1 = new Compte();
        compte1.setSolde(2500.75f);
        compte1.setDateCreation("2024-01-10");
        compte1.setType("COURANT");

        Compte compte2 = new Compte();
        compte2.setSolde(5000.00f);
        compte2.setDateCreation("2024-01-12");
        compte2.setType("EPARGNE");

        Compte compte3 = new Compte();
        compte3.setSolde(1200.50f);
        compte3.setDateCreation("2024-01-15");
        compte3.setType("COURANT");

        compteRepository.save(compte1);
        compteRepository.save(compte2);
        compteRepository.save(compte3);

        System.out.println("Sample data initialized:");
        System.out.println("- 3 accounts created");
        System.out.println("- Total balance: " + (2500.75 + 5000.00 + 1200.50));
    }
}
