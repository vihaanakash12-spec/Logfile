# Java Log Monitor

A small Java college project that runs a log-monitoring webpage in Chrome.

## Requirements

- Java 11 or newer

## Run in Chrome

```text
javac hello.java
java hello
```

Open `http://localhost:8080` in Chrome. Choose a `.log` or `.txt` file, or paste log text into the box, then click **Analyze Log**.

The program counts `INFO`, `WARN`, and `ERROR` entries. It also counts `FAILED`, `EXCEPTION`, and `TIMEOUT` as alerts.

## GitHub submission

```text
git init
git add hello.java README.md .gitignore
git commit -m "Build Java log monitoring tool"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repository>.git
git push -u origin main
```