### compiler

```bash
javac -cp "lib/*" -d out src\main\java\com\servlet\FrontControllerServlet.java src\main\java\com\annotation\Controllerako.java src\main\java\com\util\AnnotationUtil.java

```

### creation jar

```bash
jar cf mon-projet.jar -C out .
```

### transformation war

```bash
jar cf test.war -C test .
```