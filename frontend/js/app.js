// ═══════════════════════════════════════════════════════════
// SMART CMS - Shared JS Utilities
// ═══════════════════════════════════════════════════════════

const API_BASE = 'http://localhost:8080/api';

// ── Auth helpers ─────────────────────────────────────────────
const Auth = {
  save(userData) { localStorage.setItem('cms_user', JSON.stringify(userData)); },
  get() { return JSON.parse(localStorage.getItem('cms_user') || 'null'); },
  clear() { localStorage.removeItem('cms_user'); },
  isLoggedIn() { return !!this.get(); },
  getRole() { return this.get()?.role || null; },
  getUserId() { return this.get()?.userId || null; },
  requireRole(expectedRole) {
    const user = this.get();
    if (!user) { window.location.href = '/index.html'; return false; }
    if (user.role !== expectedRole) {
      Toast.show('Access denied for your role.', 'error');
      return false;
    }
    return true;
  }
};

// ── API wrapper ───────────────────────────────────────────────
const API = {
  async request(method, path, body = null, params = null) {
    let url = API_BASE + path;
    if (params) {
      const qs = new URLSearchParams(params).toString();
      url += '?' + qs;
    }
    const options = {
      method,
      headers: { 'Content-Type': 'application/json' }
    };
    if (body) options.body = JSON.stringify(body);

    const res = await fetch(url, options);
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Request failed');
    return data;
  },
  get: (path, params) => API.request('GET', path, null, params),
  post: (path, body) => API.request('POST', path, body),
  put: (path, body) => API.request('PUT', path, body),
  delete: (path, params) => API.request('DELETE', path, null, params),

  // Auth
  login: (username, password) => API.post('/auth/login', { username, password }),
  register: body => API.post('/auth/register', body),
  getProfile: id => API.get(`/auth/profile/${id}`),
  updateProfile: (id, body) => API.put(`/auth/profile/${id}`, body),

  // Complaints
  submitComplaint: body => API.post('/complaints', body),
  getAllComplaints: status => API.get('/complaints', status ? { status } : null),
  getComplaint: id => API.get(`/complaints/${id}`),
  getCitizenComplaints: id => API.get(`/complaints/citizen/${id}`),
  getOfficerComplaints: id => API.get(`/complaints/officer/${id}`),
  assignComplaint: (id, body) => API.put(`/complaints/${id}/assign`, body),
  updateComplaint: (id, body) => API.put(`/complaints/${id}/update`, body),
  submitFeedback: (id, body) => API.post(`/complaints/${id}/feedback`, body),
  trackComplaint: id => API.get(`/complaints/${id}/track`),
  getAnalytics: () => API.get('/complaints/analytics'),

  // Admin
  createOfficer: (body, adminId) => API.post(`/admin/officers?adminId=${adminId}`, body),
  getAllOfficers: dept => API.get('/admin/officers', dept ? { departmentId: dept } : null),
  getAvailableOfficers: deptId => API.get('/admin/officers/available', { departmentId: deptId }),
  deactivateOfficer: (id, adminId) => API.delete(`/admin/officers/${id}`, { adminId }),
  getDepartments: () => API.get('/admin/departments'),
  createDepartment: (body, adminId) => API.post(`/admin/departments?adminId=${adminId}`, body),
  deleteDepartment: (id, adminId) => API.delete(`/admin/departments/${id}`, { adminId }),
  getAllCitizens: () => API.get('/admin/citizens'),
  getAuditLogs: () => API.get('/admin/audit-logs'),

  // Notifications
  getNotifications: userId => API.get(`/notifications/${userId}`),
  getUnreadCount: userId => API.get(`/notifications/${userId}/count`),
  markRead: id => API.put(`/notifications/${id}/read`),

  // Util
  getCategories: () => API.get('/util/categories'),
  getStatuses: () => API.get('/util/statuses'),
};

// ── Toast notifications ───────────────────────────────────────
const Toast = {
  container: null,
  init() {
    if (!this.container) {
      this.container = document.createElement('div');
      this.container.id = 'toast-container';
      document.body.appendChild(this.container);
    }
  },
  show(message, type = 'info', duration = 3500) {
    this.init();
    const icons = { success: '✓', error: '✕', info: 'ℹ', warning: '⚠' };
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${icons[type] || 'ℹ'}</span> ${message}`;
    this.container.appendChild(toast);
    setTimeout(() => {
      toast.style.animation = 'slideOut 0.3s ease forwards';
      setTimeout(() => toast.remove(), 300);
    }, duration);
  }
};

// ── Badge helper ──────────────────────────────────────────────
function statusBadge(status) {
  const s = (status || '').toLowerCase().replace(/_/g, '_');
  const labels = {
    submitted: 'Submitted', assigned_to_department: 'Dept. Assigned',
    assigned_to_officer: 'Officer Assigned', in_progress: 'In Progress',
    resolved: 'Resolved', closed: 'Closed',
    escalated: '⚠ Escalated', rejected: 'Rejected'
  };
  return `<span class="badge badge-${s}">${labels[s] || status}</span>`;
}

function priorityBadge(priority) {
  const p = (priority || '').toLowerCase();
  return `<span class="badge badge-${p}">${priority}</span>`;
}

function priorityDot(priority) {
  return `<span class="priority-dot ${priority}"></span>`;
}

// ── Date formatter ────────────────────────────────────────────
function formatDate(iso) {
  if (!iso) return 'N/A';
  const d = new Date(iso);
  return d.toLocaleString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}
function timeAgo(iso) {
  if (!iso) return '';
  const now = Date.now(), then = new Date(iso).getTime();
  const diff = Math.floor((now - then) / 1000);
  if (diff < 60) return 'Just now';
  if (diff < 3600) return Math.floor(diff / 60) + 'm ago';
  if (diff < 86400) return Math.floor(diff / 3600) + 'h ago';
  return Math.floor(diff / 86400) + 'd ago';
}

// ── Navbar renderer ───────────────────────────────────────────
function renderNavbar(portalName) {
  const user = Auth.get();
  if (!user) return;
  const initials = (user.fullName || user.username).split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  document.getElementById('nav-user-name').textContent = user.fullName || user.username;
  document.getElementById('nav-avatar').textContent = initials;
  if (document.getElementById('nav-portal-name')) {
    document.getElementById('nav-portal-name').textContent = portalName;
  }
  // Load unread count
  if (user.userId) {
    API.getUnreadCount(user.userId).then(r => {
      const badge = document.getElementById('notif-badge');
      if (badge && r.unreadCount > 0) {
        badge.textContent = r.unreadCount;
        badge.classList.remove('hidden');
      }
    }).catch(() => { });
  }
}

function logout() {
  Auth.clear();
  window.location.href = '../index.html';
}

// ── Modal helpers ─────────────────────────────────────────────
function openModal(id) { document.getElementById(id).classList.add('open'); }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }

// ── Loading overlay ───────────────────────────────────────────
const Loader = {
  show() { document.querySelector('.loading-overlay')?.classList.add('active'); },
  hide() { document.querySelector('.loading-overlay')?.classList.remove('active'); }
};

// ── Confirm dialog ────────────────────────────────────────────
function confirmAction(message) {
  return window.confirm(message);
}

// ── Category label lookup ────────────────────────────────────
const CATEGORY_LABELS = {
  GARBAGE_COLLECTION: '🗑 Garbage Collection',
  POTHOLE: '🕳 Pothole / Road Damage',
  BROKEN_STREETLIGHT: '💡 Broken Streetlight',
  WATER_LEAKAGE: '💧 Water Leakage',
  DRAINAGE_ISSUE: '🌊 Drainage Issue',
  ILLEGAL_DUMPING: '🚯 Illegal Dumping',
  NOISE_COMPLAINT: '🔊 Noise Complaint',
  TREE_HAZARD: '🌳 Tree Hazard',
  ENCROACHMENT: '🚧 Encroachment',
  OTHER: '📋 Other'
};

// ── Complaint card builder ────────────────────────────────────
function buildComplaintCard(c, onclick) {
  const catLabel = CATEGORY_LABELS[c.category] || c.category;
  const escalated = c.escalated ? `<span class="escalated-ribbon">ESCALATED</span>` : '';
  return `
    <div class="complaint-card" onclick="${onclick}('${c.complaintId}')">
      ${escalated}
      <div class="cc-header">
        <div>
          <div class="cc-id">${c.complaintId}</div>
          <div class="cc-title">${escapeHtml(c.title)}</div>
        </div>
        <div style="display:flex;flex-direction:column;gap:6px;align-items:flex-end">
          ${statusBadge(c.status)}
          ${priorityBadge(c.priority)}
        </div>
      </div>
      <div class="cc-desc">${escapeHtml(c.description?.substring(0, 150) || '')}${c.description?.length > 150 ? '...' : ''}</div>
      <div class="cc-meta">
        <span>📂 ${catLabel}</span>
        <span>📍 ${escapeHtml(c.ward || '')}${c.city ? ', ' + escapeHtml(c.city) : ''}</span>
        <span>🕐 ${timeAgo(c.createdAt)}</span>
        ${c.assignedOfficerName ? `<span>👤 ${escapeHtml(c.assignedOfficerName)}</span>` : ''}
      </div>
    </div>`;
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
