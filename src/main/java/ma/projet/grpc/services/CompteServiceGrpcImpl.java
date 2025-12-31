package ma.projet.grpc.services;

import io.grpc.stub.StreamObserver;
import ma.projet.grpc.entities.Compte;
import ma.projet.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class CompteServiceGrpcImpl extends CompteServiceGrpc.CompteServiceImplBase {

    @Autowired
    private CompteService compteService;

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        try {
            List<Compte> comptes = compteService.findAllComptes();
            List<ma.projet.grpc.stubs.Compte> grpcComptes = comptes.stream()
                .map(this::convertToGrpcCompte)
                .collect(Collectors.toList());

            GetAllComptesResponse response = GetAllComptesResponse.newBuilder()
                .addAllComptes(grpcComptes)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        try {
            Compte compte = compteService.findCompteById(request.getId());
            if (compte != null) {
                ma.projet.grpc.stubs.Compte grpcCompte = convertToGrpcCompte(compte);
                GetCompteByIdResponse response = GetCompteByIdResponse.newBuilder()
                    .setCompte(grpcCompte)
                    .build();
                responseObserver.onNext(response);
            } else {
                responseObserver.onError(new RuntimeException("Compte not found"));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        try {
            List<Compte> comptes = compteService.findAllComptes();
            int count = comptes.size();
            double sum = comptes.stream().mapToDouble(Compte::getSolde).sum();
            double average = count > 0 ? sum / count : 0.0;

            SoldeStats stats = SoldeStats.newBuilder()
                .setCount(count)
                .setSum((float) sum)
                .setAverage((float) average)
                .build();

            GetTotalSoldeResponse response = GetTotalSoldeResponse.newBuilder()
                .setStats(stats)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        try {
            CompteRequest compteRequest = request.getCompte();
            Compte compte = new Compte();
            compte.setSolde(compteRequest.getSolde());
            compte.setDateCreation(compteRequest.getDateCreation());
            compte.setType(convertTypeCompteToString(compteRequest.getType()));

            Compte savedCompte = compteService.saveCompte(compte);
            ma.projet.grpc.stubs.Compte grpcCompte = convertToGrpcCompte(savedCompte);

            SaveCompteResponse response = SaveCompteResponse.newBuilder()
                .setCompte(grpcCompte)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private ma.projet.grpc.stubs.Compte convertToGrpcCompte(Compte compte) {
        return ma.projet.grpc.stubs.Compte.newBuilder()
            .setId(compte.getId())
            .setSolde(compte.getSolde())
            .setDateCreation(compte.getDateCreation())
            .setType(convertStringToTypeCompte(compte.getType()))
            .build();
    }

    private TypeCompte convertStringToTypeCompte(String type) {
        if ("COURANT".equalsIgnoreCase(type)) {
            return TypeCompte.COURANT;
        } else if ("EPARGNE".equalsIgnoreCase(type)) {
            return TypeCompte.EPARGNE;
        }
        return TypeCompte.COURANT; // default
    }

    private String convertTypeCompteToString(TypeCompte type) {
        switch (type) {
            case COURANT:
                return "COURANT";
            case EPARGNE:
                return "EPARGNE";
            default:
                return "COURANT";
        }
    }
}
