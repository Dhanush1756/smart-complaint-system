package com.smartcms.service;

import com.smartcms.model.*;
import com.smartcms.repository.*;
import com.smartcms.util.IdGenerator;
import com.smartcms.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds initial data and loads persisted data from files on startup.
 */
@Service
public class DataInitializerService {

        private static final Logger log = LoggerFactory.getLogger(DataInitializerService.class);

        private final UserRepository userRepo;
        private final ComplaintRepository complaintRepo;
        private final DepartmentRepository deptRepo;
        private final NotificationRepository notifRepo;
        private final AuditLogRepository auditRepo;

        public DataInitializerService(UserRepository userRepo,
                        ComplaintRepository complaintRepo,
                        DepartmentRepository deptRepo,
                        NotificationRepository notifRepo,
                        AuditLogRepository auditRepo) {
                this.userRepo = userRepo;
                this.complaintRepo = complaintRepo;
                this.deptRepo = deptRepo;
                this.notifRepo = notifRepo;
                this.auditRepo = auditRepo;
        }

        public void initializeData() {
                // Load persisted data first
                deptRepo.loadFromFile();
                userRepo.loadFromFile();
                complaintRepo.loadFromFile();
                notifRepo.loadFromFile();
                auditRepo.loadFromFile();

                // Seed if empty
                if (deptRepo.findAll().isEmpty())
                        seedDepartments();
                if (userRepo.count() == 0)
                        seedUsers();
                // if (complaintRepo.count() == 0)
                // seedComplaints();

                // Sync ID counters
                int maxComplaint = complaintRepo.findAll().stream()
                                .mapToInt(c -> {
                                        try {
                                                return Integer.parseInt(c.getComplaintId().split("-")[2]);
                                        } catch (Exception e) {
                                                return 0;
                                        }
                                }).max().orElse(0);
                IdGenerator.initComplaintCounter(maxComplaint + 1);

                log.info("Data initialized: {} departments, {} users, {} complaints",
                                deptRepo.findAll().size(), userRepo.count(), complaintRepo.count());
        }

        private void seedDepartments() {
                List<Department> depts = List.of(
                                dept("DEPT-001", "SANITATION", "Sanitation Department",
                                                "Handles garbage and waste management"),
                                dept("DEPT-002", "ROADS", "Roads & Infrastructure", "Handles potholes and road damage"),
                                dept("DEPT-003", "ELECTRICITY", "Electricity Department",
                                                "Handles streetlights and power issues"),
                                dept("DEPT-004", "WATER", "Water Supply Department",
                                                "Handles water leakage and supply"),
                                dept("DEPT-005", "DRAINAGE", "Drainage Department",
                                                "Handles drainage and sewage issues"),
                                dept("DEPT-006", "ENVIRONMENT", "Environment Department",
                                                "Handles noise and pollution complaints"),
                                dept("DEPT-007", "PARKS", "Parks & Trees", "Handles parks and tree hazards"),
                                dept("DEPT-008", "ENFORCEMENT", "Civil Enforcement",
                                                "Handles encroachment and violations"),
                                dept("DEPT-009", "GENERAL", "General Department", "Handles miscellaneous complaints"));
                depts.forEach(deptRepo::save);
                log.info("Seeded {} departments", depts.size());
        }

        private Department dept(String id, String code, String name, String desc) {
                Department d = new Department(id, code, name, desc);
                return d;
        }

        private void seedUsers() {
                // Admin
                Admin admin = new Admin("ADM-0001", "admin",
                                PasswordUtil.hashPassword("admin123"),
                                "admin@smartcms.gov.in", "9000000001", "System Administrator",
                                "Chief Municipal Administrator");
                userRepo.save(admin);

                // Officers
                Officer o1 = new Officer("OFF-0001", "officer1",
                                PasswordUtil.hashPassword("officer123"),
                                "raj.kumar@smartcms.gov.in", "9000000002", "Raj Kumar",
                                "EMP-001", "DEPT-001", "Sanitation Department", "Senior Officer");
                Officer o2 = new Officer("OFF-0002", "officer2",
                                PasswordUtil.hashPassword("officer123"),
                                "priya.sharma@smartcms.gov.in", "9000000003", "Priya Sharma",
                                "EMP-002", "DEPT-002", "Roads & Infrastructure", "Field Officer");
                Officer o3 = new Officer("OFF-0003", "officer3",
                                PasswordUtil.hashPassword("officer123"),
                                "arun.verma@smartcms.gov.in", "9000000004", "Arun Verma",
                                "EMP-003", "DEPT-003", "Electricity Department", "Junior Officer");
                Officer o4 = new Officer("OFF-0004", "officer4",
                                PasswordUtil.hashPassword("officer123"),
                                "meena.nair@smartcms.gov.in", "9000000005", "Meena Nair",
                                "EMP-004", "DEPT-004", "Water Supply Department", "Senior Officer");

                userRepo.save(o1);
                userRepo.save(o2);
                userRepo.save(o3);
                userRepo.save(o4);

                // Link officers to departments
                deptRepo.findByCode("SANITATION").ifPresent(d -> {
                        d.addOfficer("OFF-0001");
                        deptRepo.save(d);
                });
                deptRepo.findByCode("ROADS").ifPresent(d -> {
                        d.addOfficer("OFF-0002");
                        deptRepo.save(d);
                });
                deptRepo.findByCode("ELECTRICITY").ifPresent(d -> {
                        d.addOfficer("OFF-0003");
                        deptRepo.save(d);
                });
                deptRepo.findByCode("WATER").ifPresent(d -> {
                        d.addOfficer("OFF-0004");
                        deptRepo.save(d);
                });

                // Citizens
                Citizen c1 = new Citizen("CIT-0001", "citizen1",
                                PasswordUtil.hashPassword("citizen123"),
                                "ramu.s@gmail.com", "9111111001", "Ramu Shankar",
                                "12, MG Road", "Ward 5", "Bengaluru", "560001");
                Citizen c2 = new Citizen("CIT-0002", "citizen2",
                                PasswordUtil.hashPassword("citizen123"),
                                "divya.m@gmail.com", "9111111002", "Divya Menon",
                                "45, Brigade Road", "Ward 8", "Bengaluru", "560001");

                userRepo.save(c1);
                userRepo.save(c2);
                log.info("Seeded admin, 4 officers, 2 citizens");
        }

        private void seedComplaints() {
                // Seed 6 sample complaints
                Complaint c1 = new Complaint("CMP-2026-0001", "CIT-0001", "Ramu Shankar",
                                ComplaintCategory.GARBAGE_COLLECTION,
                                "Garbage not collected for 3 days",
                                "Garbage bins on MG Road have not been cleared for 3 days. Severe stench and health hazard.",
                                "MG Road, Near Bus Stop", "Ward 5", "Bengaluru");
                c1.setPriority(Priority.HIGH);
                c1.setDepartmentId("DEPT-001");
                c1.setDepartmentName("Sanitation Department");
                c1.setAssignedOfficerId("OFF-0001");
                c1.setAssignedOfficerName("Raj Kumar");
                c1.addHistoryEntry("SYSTEM", "Auto-assigned to Sanitation Department",
                                ComplaintStatus.ASSIGNED_TO_DEPARTMENT);
                c1.addHistoryEntry("ADM-0001", "Assigned to Raj Kumar", ComplaintStatus.ASSIGNED_TO_OFFICER);
                c1.setDeadline(LocalDateTime.now().plusHours(24));
                complaintRepo.save(c1);

                Complaint c2 = new Complaint("CMP-2026-0002", "CIT-0001", "Ramu Shankar",
                                ComplaintCategory.POTHOLE,
                                "Large pothole on main road",
                                "There is a large pothole near Ward 5 junction causing accidents.",
                                "Ward 5 Junction", "Ward 5", "Bengaluru");
                c2.setPriority(Priority.MEDIUM);
                c2.setDepartmentId("DEPT-002");
                c2.setDepartmentName("Roads & Infrastructure");
                c2.addHistoryEntry("SYSTEM", "Auto-assigned to Roads Department",
                                ComplaintStatus.ASSIGNED_TO_DEPARTMENT);
                complaintRepo.save(c2);

                Complaint c3 = new Complaint("CMP-2026-0003", "CIT-0002", "Divya Menon",
                                ComplaintCategory.BROKEN_STREETLIGHT,
                                "Streetlight not working for a week",
                                "Three streetlights on Brigade Road are non-functional since last week. Safety hazard at night.",
                                "Brigade Road, Sector 2", "Ward 8", "Bengaluru");
                c3.setPriority(Priority.MEDIUM);
                c3.setDepartmentId("DEPT-003");
                c3.setDepartmentName("Electricity Department");
                c3.setAssignedOfficerId("OFF-0003");
                c3.setAssignedOfficerName("Arun Verma");
                c3.addHistoryEntry("SYSTEM", "Auto-assigned to Electricity Department",
                                ComplaintStatus.ASSIGNED_TO_DEPARTMENT);
                c3.addHistoryEntry("ADM-0001", "Assigned to Arun Verma", ComplaintStatus.ASSIGNED_TO_OFFICER);
                c3.addHistoryEntry("OFF-0003", "Inspection done, parts ordered", ComplaintStatus.IN_PROGRESS);
                complaintRepo.save(c3);

                Complaint c4 = new Complaint("CMP-2026-0004", "CIT-0002", "Divya Menon",
                                ComplaintCategory.WATER_LEAKAGE,
                                "Water pipe burst on main street",
                                "A water pipe has burst near Brigade Road causing flooding on the road.",
                                "Brigade Road, Main", "Ward 8", "Bengaluru");
                c4.setPriority(Priority.EMERGENCY);
                c4.setDepartmentId("DEPT-004");
                c4.setDepartmentName("Water Supply Department");
                c4.setAssignedOfficerId("OFF-0004");
                c4.setAssignedOfficerName("Meena Nair");
                c4.addHistoryEntry("SYSTEM", "Auto-assigned to Water Supply", ComplaintStatus.ASSIGNED_TO_DEPARTMENT);
                c4.addHistoryEntry("ADM-0001", "EMERGENCY - Assigned to Meena Nair",
                                ComplaintStatus.ASSIGNED_TO_OFFICER);
                c4.addHistoryEntry("OFF-0004", "Team dispatched to site", ComplaintStatus.IN_PROGRESS);
                c4.addHistoryEntry("OFF-0004", "Pipe repaired. Area cleaned.", ComplaintStatus.RESOLVED);
                c4.setResolutionNote("Burst pipe replaced with new 4-inch PVC pipe. Road cleared of water.");
                c4.setResolvedAt(LocalDateTime.now().minusHours(2));
                c4.setCitizenRating(5);
                c4.setCitizenFeedback("Excellent work! Fast response.");
                c4.addHistoryEntry("CIT-0002", "Citizen rated 5/5", ComplaintStatus.CLOSED);
                complaintRepo.save(c4);

                Complaint c5 = new Complaint("CMP-2026-0005", "CIT-0001", "Ramu Shankar",
                                ComplaintCategory.NOISE_COMPLAINT,
                                "Construction noise at night",
                                "Illegal construction work happening after 10pm causing noise disturbance.",
                                "15th Cross, Ward 5", "Ward 5", "Bengaluru");
                c5.setPriority(Priority.LOW);
                complaintRepo.save(c5);

                Complaint c6 = new Complaint("CMP-2026-0006", "CIT-0002", "Divya Menon",
                                ComplaintCategory.DRAINAGE_ISSUE,
                                "Blocked drain causing flooding",
                                "Main drain at Ward 8 market is completely blocked. Wastewater is flooding the street.",
                                "Ward 8 Market Area", "Ward 8", "Bengaluru");
                c6.setPriority(Priority.HIGH);
                c6.setEscalated(true);
                c6.setDeadline(LocalDateTime.now().minusHours(5));
                c6.addHistoryEntry("SYSTEM", "Escalated: deadline breached", ComplaintStatus.ESCALATED);
                complaintRepo.save(c6);

                IdGenerator.initComplaintCounter(7);
                log.info("Seeded 6 sample complaints");
        }
}
