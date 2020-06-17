  _______           _                 _                     _                      ______     _  _  _               
 |__   __|         | |      /\       | |                   | |                    |  ____|   | |(_)| |              
    | |  ___ __  __| |_    /  \    __| |__   __ ___  _ __  | |_  _   _  _ __  ___ | |__    __| | _ | |_  ___   _ __ 
    | | / _ \\ \/ /| __|  / /\ \  / _` |\ \ / // _ \| '_ \ | __|| | | || '__|/ _ \|  __|  / _` || || __|/ _ \ | '__|
    | ||  __/ >  < | |_  / ____ \| (_| | \ V /|  __/| | | || |_ | |_| || |  |  __/| |____| (_| || || |_| (_) || |   
    |_| \___|/_/\_\ \__|/_/    \_\\__,_|  \_/  \___||_| |_| \__| \__,_||_|   \___||______|\__,_||_| \__|\___/ |_|   

Progetto di Basi di Dati, A.A. 2019/2020
Realizzato da Aloisi Giacomo e Carlassare Giulio.

PRESENTAZIONE

Lo scopo di questa applicazione è quello di creare e modificare avventure testuali in stile LibroGame.
Le meccaniche di gioco e il mantenimento dello stato della partita non sono implementati e richiedono una apposita applicazione.

AMBIENTE

Questa applicazione è stata realizzata in Kotlin, utilizzando JavaFX come backend per l'interfaccia grafica.
Per una corretta esecuzione è quindi necessario l'utilizzo di Java 8 e JavaFX.
L'utilizzo di Gradle rende più agevole sia la compilazione che la gestione delle dipendenze.

ESECUZIONE

Nella cartella sono presenti i wrapper di Gradle sia per Linux/MacOS (gradlew) sia per Windows (gradlew.bat); per eseguire un comando sarà sufficiente lanciare il wrapper corrispondente alla propria piattaforma seguito dal nome del comando, ad esempio './gradlew run' (Linux/MacOS) o 'gradlew.bat run' (Windows).

I comandi principali sono:
 * 'build' per compilare l'applicazione
 * 'run' per compilare ed eseguire l'applicazione
 * 'jar' per compilare l'applicazione ed ottenere un file JAR eseguibile, che verrà salvato nella cartella principale del progetto

Per semplicità è possibile ottenere il JAR pronto dal seguente link
https://github.com/Carlovan/text-adventure-editor/releases/download/v1.0/text-advendure-editor-1.0.jar