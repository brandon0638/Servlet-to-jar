#### compiler sprint0 avec source tomcat

```bash
javac -cp "G:\xampp\tomcat\lib\servlet-api.jar" -d build projet_sprint1\src\main\java\com\servlet\FrontControllerServlet.java
```

#### transformer en jar
```bash
jar cvf framework-Servlet.jar -C build .
```

