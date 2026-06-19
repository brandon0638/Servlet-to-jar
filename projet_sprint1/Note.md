#### compiler sprint0 avec source tomcat

```bash
javac -cp "G:\xampp\tomcat\lib\servlet-api.jar" -d build projet_sprint1\src\main\java\com\servlet\FrontControllerServlet.java
```

#### transformer en jar
```bash
jar cvf framework-Servlet.jar -C build .
```

#### Nettoyer les anciens fichiers compilés
```bash
rmdir /s /q build\classes
```

#### Recréer le dossier

```bash
mkdir build\classes
```

#### Compiler TOUS les fichiers Java

```bash
javac -cp "G:\xampp\tomcat\lib\servlet-api.jar" -d build/classes src/main/java/com/annotation/*.java src/main/java/com/servlet/*.java src/main/java/com/util/*.java src/main/java/com/test/controller/*.java
```

#### Supprimer l'ancien WAR

```bash
del war\sprint1.2.war
```

#### Supprimer l'ancien dossier classes dans le WAR
```bash
rmdir /s /q war\WEB-INF\classes
```

#### Recréer le dossier classes
```bash
mkdir war\WEB-INF\classes
```

#### Copier les nouvelles classes
```bash
xcopy /E build\classes war\WEB-INF\classes\ /Y
```

### Recréer le WAR
 ```bash
 cd war
 jar cvf sprint1.2.war *
 ```