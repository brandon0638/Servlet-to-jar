### compiler

```bash
javac -cp "lib/*" -d out src\main\java\com\servlet\FrontControllerServlet.java
```

### creation jar

```bash
jar cf mon-projet.jar -C out .
```

### transformation war

```bash
jar cf test.war -C test .
```