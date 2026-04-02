# 🏛 Smart Public Complaint Management System

A full-stack Java OOP university mini-project demonstrating all core OOP concepts
via a real-world civic complaint management platform.

---

## 🎯 OOP Concepts Demonstrated

| Concept | Where Used |
|---|---|
| **Classes & Objects** | `User`, `Complaint`, `Department`, `Notification`, `AuditLog` |
| **Inheritance** | `Citizen`, `Admin`, `Officer` all extend `User` |
| **Polymorphism** | `getDashboardData()`, `compareTo()`, Comparable, overridden methods |
| **Encapsulation** | All fields private with getters/setters throughout |
| **Abstract Classes** | `User` is abstract with abstract `getDashboardData()` |
| **Interfaces** | `Trackable`, `Resolvable`, `Notifiable` implemented by `Complaint`/`User` |
| **Packages** | `model`, `service`, `repository`, `controller`, `exception`, `util`, `thread`, `config`, `dto` |
| **Exception Handling** | 8 custom exceptions + `GlobalExceptionHandler` |
| **Multithreading** | `EscalationMonitorThread`, `NotificationDispatchThread`, `ReadWriteLock`, `AtomicInteger`, `LinkedBlockingQueue` |
| **Collections** | `HashMap`, `ArrayList`, `HashSet`, `PriorityQueue`, `TreeMap`, `CopyOnWriteArrayList`, `LinkedBlockingQueue` |
| **String Operations** | ID generation formatting, validation, description truncation |
| **File Handling** | JSON serialization via `FileStorageUtil` → `users.json`, `complaints.json`, etc. |

---

## 🗂 Project Structure

```
smart-complaint-system/
├── backend/                          ← Spring Boot Java backend
│   ├── pom.xml
│   └── src/main/java/com/smartcms/
│       ├── SmartComplaintSystemApplication.java
│       ├── model/
│       │   ├── User.java             ← Abstract base class
│       │   ├── Citizen.java          ← Extends User
│       │   ├── Admin.java            ← Extends User
│       │   ├── Officer.java          ← Extends User
│       │   ├── Complaint.java        ← Implements Trackable, Resolvable, Comparable
│       │   ├── ComplaintHistory.java
│       │   ├── Department.java
│       │   ├── Notification.java
│       │   ├── AuditLog.java
│       │   ├── Trackable.java        ← Interface
│       │   ├── Resolvable.java       ← Interface
│       │   ├── Notifiable.java       ← Interface
│       │   ├── UserRole.java
│       │   ├── ComplaintStatus.java
│       │   ├── ComplaintCategory.java
│       │   └── Priority.java
│       ├── dto/
│       │   └── Dtos.java             ← All DTOs
│       ├── exception/
│       │   ├── ComplaintNotFoundException.java
│       │   ├── UserNotFoundException.java
│       │   ├── InvalidComplaintException.java
│       │   ├── UnauthorizedAccessException.java
│       │   ├── OfficerUnavailableException.java
│       │   ├── DepartmentNotFoundException.java
│       │   ├── DuplicateUsernameException.java
│       │   ├── StorageException.java
│       │   └── GlobalExceptionHandler.java
│       ├── repository/
│       │   ├── UserRepository.java         ← HashMap + ReadWriteLock
│       │   ├── ComplaintRepository.java    ← HashMap + PriorityQueue + TreeMap
│       │   ├── DepartmentRepository.java
│       │   ├── NotificationRepository.java ← LinkedBlockingQueue
│       │   └── AuditLogRepository.java     ← CopyOnWriteArrayList
│       ├── service/
│       │   ├── UserService.java
│       │   ├── ComplaintService.java
│       │   ├── DepartmentService.java
│       │   ├── NotificationService.java
│       │   ├── AuditService.java
│       │   └── DataInitializerService.java
│       ├── controller/
│       │   ├── AuthController.java
│       │   ├── ComplaintController.java
│       │   ├── AdminController.java
│       │   ├── NotificationController.java
│       │   └── UtilController.java
│       ├── thread/
│       │   ├── EscalationMonitorThread.java   ← Runnable, daemon thread
│       │   └── NotificationDispatchThread.java ← BlockingQueue consumer
│       ├── util/
│       │   ├── IdGenerator.java    ← AtomicInteger, String formatting
│       │   ├── PasswordUtil.java   ← SHA-256 hashing
│       │   ├── DateUtil.java
│       │   └── FileStorageUtil.java ← JSON file I/O
│       └── config/
│           └── CorsConfig.java
│
├── frontend/                          ← HTML/CSS/JS frontend
│   ├── index.html                     ← Login & registration page
│   ├── css/
│   │   └── style.css                  ← Shared styles
│   ├── js/
│   │   └── app.js                     ← API calls, auth, utilities
│   └── pages/
│       ├── citizen.html               ← Citizen portal
│       ├── admin.html                 ← Admin portal
│       └── officer.html               ← Officer portal
│
└── data/                              ← Auto-created on first run
    ├── users.json
    ├── complaints.json
    ├── departments.json
    ├── notifications.json
    ├── audit_log.json
    └── system.log
```

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- Any modern web browser

### Step 1: Start the Backend

```bash
cd smart-complaint-system/backend
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**

You'll see:
```
=======================================================
  SMART PUBLIC COMPLAINT MANAGEMENT SYSTEM STARTED
  API: http://localhost:8080/api
  Background threads: EscalationMonitor, NotificationDispatcher
=======================================================
```

### Step 2: Open the Frontend

Simply open `frontend/index.html` in your browser.

**Or** use VS Code Live Server / any static HTTP server:
```bash
cd smart-complaint-system/frontend
python3 -m http.server 3000
# Then visit http://localhost:3000
```

---

## 🔑 Default Login Credentials

| Portal | Username | Password |
|--------|----------|----------|
| **Citizen** | citizen1 | citizen123 |
| **Citizen** | citizen2 | citizen123 |
| **Admin** | admin | admin123 |
| **Officer** | officer1 | officer123 |
| **Officer** | officer2 | officer123 |
| **Officer** | officer3 | officer123 |
| **Officer** | officer4 | officer123 |

---

## 🌐 REST API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/register` | Register citizen |
| GET | `/api/auth/profile/{id}` | Get profile |
| PUT | `/api/auth/profile/{id}` | Update profile |

### Complaints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/complaints` | Submit complaint |
| GET | `/api/complaints` | All complaints (Admin) |
| GET | `/api/complaints/{id}` | Get by ID |
| GET | `/api/complaints/citizen/{id}` | By citizen |
| GET | `/api/complaints/officer/{id}` | By officer |
| PUT | `/api/complaints/{id}/assign` | Assign (Admin) |
| PUT | `/api/complaints/{id}/update` | Update progress (Officer) |
| POST | `/api/complaints/{id}/feedback` | Submit feedback (Citizen) |
| GET | `/api/complaints/{id}/track` | Track complaint |
| GET | `/api/complaints/analytics` | Analytics |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/officers` | All officers |
| POST | `/api/admin/officers` | Create officer |
| DELETE | `/api/admin/officers/{id}` | Deactivate officer |
| GET | `/api/admin/departments` | All departments |
| POST | `/api/admin/departments` | Create department |
| DELETE | `/api/admin/departments/{id}` | Delete department |
| GET | `/api/admin/citizens` | All citizens |
| GET | `/api/admin/audit-logs` | Audit logs |

### Notifications
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notifications/{userId}` | Get notifications |
| GET | `/api/notifications/{userId}/unread` | Unread |
| GET | `/api/notifications/{userId}/count` | Unread count |
| PUT | `/api/notifications/{id}/read` | Mark read |

### Utilities
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/util/categories` | Complaint categories |
| GET | `/api/util/statuses` | Complaint statuses |
| GET | `/api/util/health` | Health check |

---

## 🔄 Complaint Lifecycle

```
SUBMITTED → ASSIGNED_TO_DEPARTMENT → ASSIGNED_TO_OFFICER → IN_PROGRESS → RESOLVED → CLOSED
                                                          ↓
                                                      ESCALATED (if overdue)
```

---

## 🧵 Multithreading Architecture

```
Main Thread (Spring Boot)
    ├── EscalationMonitorThread (daemon)
    │     Polls every 30 min, finds overdue complaints, escalates + notifies admin
    │
    └── NotificationDispatchThread (daemon)
          Consumes from LinkedBlockingQueue, dispatches notifications async
```

---

## 💾 Data Persistence (File Storage)

All data stored as JSON in the `./data/` directory:
- **users.json** — All users (citizens, admins, officers)
- **complaints.json** — All complaints with full history
- **departments.json** — Department configuration
- **notifications.json** — Notification records
- **audit_log.json** — System-wide audit trail
- **system.log** — Application logs

Data persists across restarts. Delete the `data/` folder to reset to seed data.

---

## 📊 Sample Data (Auto-seeded)

- 9 municipal departments
- 1 admin, 4 officers, 2 citizens
- 6 sample complaints (various statuses including escalated, resolved, in-progress)

---

## 🏗 UML Class Hierarchy

```
                    ┌──────────────┐
                    │  <<abstract>>│
                    │     User     │ implements Notifiable
                    └──────┬───────┘
              ┌────────────┼────────────┐
         ┌────▼──┐    ┌────▼───┐   ┌───▼────┐
         │Citizen│    │ Admin  │   │Officer │
         └───────┘    └────────┘   └────────┘

         ┌─────────────────────────────────────┐
         │           Complaint                 │
         │  implements Trackable,              │
         │            Resolvable,              │
         │            Comparable<Complaint>    │
         └─────────────────────────────────────┘
                    contains →  List<ComplaintHistory>

Interfaces:
  Trackable   → getComplaintId(), getStatus(), getTrackingSummary()
  Resolvable  → resolve(), isResolved(), getResolutionNote()
  Notifiable  → getEmail(), getPhone(), getFullName()
```

---

## 👨‍💻 Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Build | Maven |
| Persistence | JSON Files (Jackson) |
| Frontend | HTML5, CSS3, Vanilla JS |
| API Style | REST |
| Security | SHA-256 password hashing |
