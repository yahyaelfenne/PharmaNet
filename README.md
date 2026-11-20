# PharmaNet

Application Java de gestion d'un réseau de pharmacies. L'interface s'utilise entièrement dans le terminal.

## Démarrage

Sous Windows, extrayez l'archive, ouvrez un terminal dans le dossier et exécutez `.\lancer.cmd`. Java 17 ou une version plus récente est requis. Le lanceur utilise JAVA_HOME, le runtime Java de VS Code si disponible, ou la commande java du PATH.

Avec Java 17 ou ultérieur sur le PATH, exécutez `java -jar PharmaNet.jar` sous Windows, macOS ou Linux.

## Comptes de démonstration

| Espace | Adresse | Mot de passe |
| --- | --- | --- |
| Client | sara@mail.com | client123 |
| Pharmacien | karim@pharmanet.ma | secret123 |
| Administrateur | admin@pharmanet.ma | admin123 |

- Client. Rechercher un médicament disponible et les pharmacies de garde, avec un filtre par ville.
- Pharmacien. Consulter et ajuster le stock, enregistrer une vente, consulter les dates d'expiration et ouvrir l'historique.
- Administrateur. Activer ou désactiver les comptes et pharmacies et définir une période de garde par pharmacie.

Les ventes contrôlent la disponibilité, la date d'expiration et la présence d'une ordonnance lorsqu'elle est requise. Les prix et comptes fournis sont fictifs.

La sauvegarde locale `pharmanet.dat` est créée à la première modification. Gardez ce fichier pour reprendre votre session. Le fichier `pharmanet.dat.lock` empêche deux instances de modifier la même sauvegarde simultanément. Pour une autre sauvegarde, passez un chemin à la commande, par exemple `java -jar PharmaNet.jar donnees-test.dat`.

## Recompiler à partir du code source

PowerShell avec un JDK 17 ou ultérieur.

```powershell
$src = Get-ChildItem src -Recurse -Filter *.java
New-Item -ItemType Directory -Force build | Out-Null
javac --release 17 -encoding UTF-8 -d build $src.FullName
java -cp build ma.emsi.pharmanet.Etape2Test
jar --create --file PharmaNet.jar --main-class ma.emsi.pharmanet.Main -C build .
```

