 ## Sprint 1
 - mamorona propre annotation ako

 - creer annotation pour class controller dans un package annotation-> (au niveau framework)
 - creer controller dans test et mettre notre annotation creer ici , les classes annoter sont reconnues dans notre framework
 - il y a des bouits de code soit au demarage de l'appilcation web que s'est executer 

 [code] -> demarage App web (utiliser un listener anaty servlet)
        -> premier appel du frontservlet(utiliser methode init appel une fois)

N.B: le code liste tous les classes dasn la classPath
    avec le code il faut savoir tous les controllers

Frontservletcontroller: variable ou attribut List<string> Listecontroller
            quand app test est executer frontservlet est appel et init est appel en premier, 

[annotationcontroller] -> tous les class dans classPath
                       -> donner une liste de tous les packages

- ajouter dans web.xml un variable package qui contient les controllers
- il faut avoir un methode generaliser qui verifie si un annotatoin existe ou pas et ou il est au niveau class ou methode
