package ma.emsi.pharmanet.console;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;

final class FichierDonnees implements AutoCloseable {
    private final Path chemin;
    private final FileChannel verrou;

    FichierDonnees(Path chemin) throws IOException {
        this.chemin = chemin.toAbsolutePath().normalize();
        Files.createDirectories(this.chemin.getParent());
        Path fichierVerrou = this.chemin.resolveSibling(this.chemin.getFileName() + ".lock");
        verrou = FileChannel.open(fichierVerrou, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            if (verrou.tryLock() == null) {
                throw new IOException("Les données sont déjà utilisées par une autre instance.");
            }
        } catch (IOException | RuntimeException e) {
            verrou.close();
            throw e;
        }
    }

    DonneesDemo charger() throws IOException, ClassNotFoundException {
        if (Files.notExists(chemin)) return new DonneesDemo();
        try (ObjectInputStream entree = new ObjectInputStream(Files.newInputStream(chemin))) {
            entree.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
                    "maxdepth=40;ma.emsi.pharmanet.console.DonneesDemo;ma.emsi.pharmanet.model.*;"
                    + "java.util.ArrayList;java.lang.Object;java.time.*;!*"));
            if (entree.readObject() instanceof DonneesDemo donnees) return donnees;
            throw new IOException("Format de sauvegarde inconnu.");
        }
    }

    void sauvegarder(DonneesDemo donnees) throws IOException {
        Path temporaire = Files.createTempFile(chemin.getParent(), "pharmanet-", ".tmp");
        try {
            try (ObjectOutputStream sortie = new ObjectOutputStream(Files.newOutputStream(temporaire))) {
                sortie.writeObject(donnees);
            }
            Files.move(temporaire, chemin, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temporaire);
        }
    }

    @Override
    public void close() throws IOException {
        verrou.close();
    }
}
