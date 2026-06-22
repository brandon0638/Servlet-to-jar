 ## Sprint 1
 - mamorona propre annotation ako

 - creer annotation pour class controller dans un package annotation-> (au niveau framework)
 - creer controller dans test et mettre notre annotation creer ici , les classes annoter sont reconnues dans notre framework
 - il y a des bouts de code soit au demarage de l'appilcation web que s'est executer 

 [code] -> demarage App web (utiliser un listener anaty servlet)
        -> premier appel du frontservlet(utiliser methode init appel une fois)

N.B: le code liste tous les classes dasn la classPath
    avec le code il faut savoir tous les controllers

Frontservletcontroller: variable ou attribut List<string> Listecontroller
            quand app test est executer frontservlet est appel et init est appel en premier, 

[annotationcontroller] -> tous les class dans classPath
                       -> donner une liste de tous les packages

- ajouter dans web.xml un variable "package" qui contient les controllers
- il faut avoir un methode generaliser qui verifie si un annotatoin existe ou pas et ou il est au niveau class ou methode


## Sprint 2

- creer annotation type method(mila variable)
    ex: EmpController deja annoter avec notre annotation
        on a une methode list et par exemple l'annoter avec par exemple @urlMapping qui prend parametre("/emp/list")
        on peut faire aussi url="/emp/list"
        
    le methode verifiena oe misy annotation url ve sa ts

    on verifie juste par ca par exmple: /emp/list est dans la methode EmpController->liste ; /emp/new est dans la methode EmpController-.create

    on a besoin du liste url donc en resumer

    petit modif: quand url n;est pas dans url donc faire juste un throws urk n'existe pas
    quand on connais pas url throws exception dire les listes des urls disponible, si on connais afficher juste la methode associer a ce url