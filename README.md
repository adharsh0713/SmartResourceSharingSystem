# 📚 **Smart Resource Sharing System**

*A Java Application with Console & GUI Implementations*

---

## **Overview**

The **Smart Resource Sharing System** is a Java-based desktop application designed to simplify the sharing and borrowing of academic resources within a campus environment.

The project offers:

* ✅ **Console-based implementation (CLI)**
* ✅ **GUI-based implementation (AWT + Swing)**
* ✅ **Core OOP + Java syllabus concepts**
* ✅ **Full local data persistence using File I/O + Serialization**
* ✅ **Multithreaded background services**
* ✅ **Generics, Collections, java.time, and regex integration**

This project is designed to demonstrate **clean architecture**, **object-oriented principles**, and strong programming fundamentals.

---

## **Key Features**

### **1. User Authentication**

* Register new users
* Login with regex-validated credentials
* Email format & password strength validation

---

### **2. Resource Management**

Users can:

* Add academic resources (books, tools, lab modules, kits, etc.)
* View available resources
* Request to borrow
* Approve or reject requests (lenders)
* View transaction history

Uses:

* Generics (`Resource<T>`)
* Collections (ArrayList, HashMap)
* Comparators for sorting

---

### **3. Borrowing System with Date Handling**

Uses **java.time API** for:

* Borrow date
* Due date calculation
* Duration & Period
* Overdue detection

---

### **4. Multithreading**

Background threads handle:

* Auto-saving data every 30 seconds
* Checking overdue items
* GUI notification pop-ups

---

### **5. File I/O + Serialization**

Persistent storage using:

```
users.dat
items.dat
requests.dat
transactions.dat
```

Uses:

* ObjectInputStream
* ObjectOutputStream
* FileInputStream / FileOutputStream

---

### **6. Dual Interface**

#### **A. Console Version (CLI)**

* Menu-driven
* Scanner-based input
* Console tables for output
* StringTokenizer for parsing

#### **B. GUI Version (AWT + Swing)**

* Login window
* Dashboard
* Add Resource Form
* Browse Items Table (JTable)
* Request Management Window
* Notification dialogs

---

## **Tech Stack / Concepts Used**

| Category                  | Concepts                                                                |
| ------------------------- | ----------------------------------------------------------------------- |
| **OOP**                   | Classes, Objects, Abstraction, Encapsulation, Inheritance, Polymorphism |
| **Advanced OOP**          | Interfaces, Abstract classes, Packages                                  |
| **Exception Handling**    | try-catch-finally, custom exceptions, multi-catch                       |
| **Multithreading**        | Thread class, Runnable interface, synchronization                       |
| **Data Handling**         | java.io, Serialization, Character & Byte streams                        |
| **Regex**                 | Pattern + Matcher for validation                                        |
| **Date & Time**           | java.time: LocalDate, LocalDateTime, Period, Duration                   |
| **Collections Framework** | ArrayList, HashMap, TreeMap, PriorityQueue, Comparator                  |
| **Generics**              | Generic Resource class, wildcard collections                            |
| **User Interface**        | AWT, Swing (JFrame, JPanel, JButton, JTable, etc.)                      |
| **Console I/O**           | Scanner, StringTokenizer                                                |

---

## **Learning Outcomes**

By completing this project, we demonstrated skills in:

* Object-oriented design
* Java Swing GUI development
* Multithreaded programming
* File handling & persistence
* Data structures and generics
* Clean architecture & modular programming
* Date/time handling for real-world logic
* Regex-based input validation

Perfect for **academic submission**, **viva**, **resume**, and **GitHub portfolio**.

---

## **Credits**

Developed as part of the Object Oriented Programming Curriculum (Academic Year 2025–26).

---

## Author

**Adharsh Baswaraj** – [adharshbaswaraj@gmail.com]  
GitHub: [https://github.com/adharsh0713]

**Koushik Jonnala** – [koushikjonnala@gmail.com]  
GitHub: [https://github.com/koushik632]

---

## **License**

This project is licensed under the [MIT License](LICENSE) – see the LICENSE file for details.
