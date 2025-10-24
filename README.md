<h3 align="center">Library Management System (LMS)</h3>

<div align="center">

[![Status](https://img.shields.io/badge/status-active-success.svg)]()
[![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)]()
[![Swing](https://img.shields.io/badge/Swing-GUI-orange.svg)]()
[![AWT](https://img.shields.io/badge/AWT-Graphics-red.svg)]()
[![TXT](https://img.shields.io/badge/Storage-TXT-green.svg)]()
[![JAR](https://img.shields.io/badge/Package-JAR-blue.svg)]()

</div>

---

<p align="center"> n
A simple **Java-based Library Management System (LMS)** with a Swing GUI for managing books, members, librarians, and transactions.  
<br>
Easily runnable via `.bat` files, directly from the terminal, or as a compiled `.jar` file.
</p>

---

## 📝 Table of Contents

- [About](#about)
- [Folder Structure](#structure)
- [Getting Started](#getting_started)
  - [Prerequisites](#prerequisites)
  - [Running the Project](#running)
  - [Building the JAR](#build)
- [Usage](#usage)
- [Built Using](#built_using)
- [Authors](#authors)
- [Acknowledgments](#acknowledgement)

---

## 🧐 About <a name="about"></a>

This project is a **desktop-based Library Management System (LMS)** built in **Java** using **Swing** for the GUI.  
It allows librarians to:
- Manage books and members  
- Record transactions  
- Handle logins and view reports  

The application supports local data storage through text or serialized files and can be extended to use databases.

---

## 📁 Folder Structure <a name="structure"></a>

```
LMS/
│
├── bin/                    # Compiled .class files (auto-generated after build)
├── data/                   # Application data (book list, member data, etc.)
├── src/                    # Source code files
│   ├── Book.java
│   ├── FileHandler.java
│   ├── Librarian.java
│   ├── LibraryException.java
│   ├── LibraryGUI.java
│   ├── LoginScreen.java
│   ├── Main.java
│   ├── Member.java
│   └── Transaction.java
│
├── buildJar.bat            # Builds the JAR file
├── run.bat                 # Runs the program easily via batch script
├── manifest.txt            # Manifest file for JAR packaging
├── .gitignore
└── README.md
```

---

## 🏁 Getting Started <a name="getting_started"></a>

These instructions will help you set up, run, and build the LMS project on your local system.

### ✅ Prerequisites <a name="prerequisites"></a>

Make sure you have the following installed:

- **Java JDK 8 or higher**
- **Command Prompt / Terminal access**
- (Optional) **VS Code** or **Eclipse** if you prefer running from an IDE

To verify Java installation:
```bash
java -version
javac -version
```

---

## ▶ Running the Project <a name="running"></a>

You can run the LMS in **three ways**:

---

### 🧩 1. Run Normally (from source)
If you just want to compile and run it manually:

```bash
cd src
javac *.java
java Main
```

---

### ⚡ 2. Run via `run.bat`
Simply double-click on the `run.bat` file or execute in terminal:

```bash
run.bat
```

> 💡 The `run.bat` file automatically compiles all Java files in `src/` and runs the main application.

---

### 🏗️ 3. Run via `.jar` File
If you already have the JAR built:

```bash
java -jar LMS.jar
```

If it’s not built yet, see the next section.

---

## 🧱 Building the JAR <a name="build"></a>

You can build the JAR manually or using the provided batch script.

### 🔹 Option 1: Use `buildJar.bat`
Just double-click the file or run:
```bash
buildJar.bat
```

This will:
1. Compile all `.java` files in `/src`
2. Create `.class` files in `/bin`
3. Package them into `LMS.jar` using `manifest.txt`

> Ensure `manifest.txt` includes the entry:
> ```
> Main-Class: Main
> ```

---

### 🔹 Option 2: Build Manually
If you want to build manually from command line:

```bash
javac -d bin src/*.java
jar cfm LMS.jar manifest.txt -C bin .
```

This creates `LMS.jar` in your root directory.

---

## 🎈 Usage <a name="usage"></a>

- Launch the app → Login screen appears.  
- Use librarian credentials to log in.  
- Perform actions like:
  - Add / Remove Books
  - Register Members
  - Record Book Transactions
  - View Reports  

---

## ⛏ Built Using <a name="built_using"></a>

- **Java SE** — Core language  
- **Swing** — GUI Framework  
- **AWT** — Event Handling & Layout  
- **File I/O** — Data persistence  

---

## ✍ Authors <a name="authors"></a>

- **[@Siddhesh Paithankar](https://github.com/siddheshpaithankar)** — Developer & Maintainer  

---

## 🎉 Acknowledgments <a name="acknowledgement"></a>

- Java Swing documentation  
- Oracle Java Tutorials  
- Inspiration from classic library management systems  

---

> 💾 **Note:**  
> Always run the project from the root directory (where `src`, `bin`, and `data` folders are located) for relative paths to work properly.
