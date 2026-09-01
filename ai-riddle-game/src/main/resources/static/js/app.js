/**
 * AI Riddle Arena - Core App Router & Utility Module
 */
const app = {
    currentView: 'dashboard',
    user: null,
    token: null,

    init() {
        this.loadSession();
        this.updateNav();

        // Initial view routing based on auth state
        if (this.token) {
            this.navigateTo('dashboard');
        } else {
            this.navigateTo('auth');
        }
    },

    loadSession() {
        this.token = localStorage.getItem('riddle_jwt_token');
        const userJson = localStorage.getItem('riddle_user');
        if (userJson) {
            try {
                this.user = JSON.parse(userJson);
            } catch (e) {
                this.user = null;
            }
        }
    },

    setSession(token, user) {
        this.token = token;
        this.user = user;
        localStorage.setItem('riddle_jwt_token', token);
        localStorage.setItem('riddle_user', JSON.stringify(user));
        this.updateNav();
    },

    clearSession() {
        this.token = null;
        this.user = null;
        localStorage.removeItem('riddle_jwt_token');
        localStorage.removeItem('riddle_user');
        this.updateNav();
        this.navigateTo('auth');
        this.showToast('Logged out successfully', 'info');
    },

    updateNav() {
        const navAuthGroup = document.getElementById('nav-auth-group');
        if (!navAuthGroup) return;

        if (this.user) {
            let adminBtnHtml = '';
            if (this.user.role === 'ROLE_ADMIN') {
                adminBtnHtml = `
                    <button class="nav-btn ${this.currentView === 'admin' ? 'active' : ''}" data-view="admin" onclick="app.navigateTo('admin')">
                        <i class="fa-solid fa-user-shield"></i> Admin Portal
                    </button>
                `;
            }

            navAuthGroup.innerHTML = `
                <button class="nav-btn ${this.currentView === 'history' ? 'active' : ''}" data-view="history" onclick="app.navigateTo('history')">
                    <i class="fa-solid fa-clock-rotate-left"></i> History
                </button>
                ${adminBtnHtml}
                <div class="user-badge">
                    <span class="user-badge-name"><i class="fa-solid fa-user-ninja"></i> ${this.escapeHtml(this.user.username)}</span>
                    <button class="btn-logout" onclick="app.clearSession()">Logout</button>
                </div>
            `;
        } else {
            navAuthGroup.innerHTML = `
                <button class="nav-btn ${this.currentView === 'auth' ? 'active' : ''}" data-view="auth" onclick="app.navigateTo('auth')">
                    <i class="fa-solid fa-right-to-bracket"></i> Login / Register
                </button>
            `;
        }
    },

    navigateTo(viewName) {
        // Protect authenticated views
        if (!this.token && viewName !== 'auth' && viewName !== 'leaderboard') {
            this.showToast('Please login to access this section', 'error');
            viewName = 'auth';
        }

        // Admin check
        if (viewName === 'admin' && (!this.user || this.user.role !== 'ROLE_ADMIN')) {
            this.showToast('Admin access required', 'error');
            viewName = 'dashboard';
        }

        this.currentView = viewName;

        // Hide all views
        document.querySelectorAll('.view-section').forEach(sec => sec.classList.add('hidden'));

        // Show target view
        const targetView = document.getElementById(`view-${viewName}`);
        if (targetView) {
            targetView.classList.remove('hidden');
        }

        // Update nav active states
        document.querySelectorAll('.nav-btn').forEach(btn => {
            if (btn.dataset.view === viewName) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });

        // Trigger view-specific lifecycle initializers
        if (viewName === 'dashboard') {
            if (window.auth) auth.loadUserProfile();
            if (window.game) game.loadCategories();
        } else if (viewName === 'leaderboard') {
            this.loadLeaderboard();
        } else if (viewName === 'history') {
            this.loadUserHistory();
        } else if (viewName === 'admin') {
            if (window.admin) admin.init();
        }
    },

    async fetchApi(url, options = {}) {
        options.headers = options.headers || {};
        if (this.token) {
            options.headers['Authorization'] = `Bearer ${this.token}`;
        }
        if (options.body && typeof options.body === 'object') {
            options.headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(options.body);
        }

        try {
            const response = await fetch(url, options);
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message || `HTTP Error ${response.status}`);
            }
            return data;
        } catch (err) {
            console.error('API Error:', err);
            throw err;
        }
    },

    async loadLeaderboard() {
        const tbody = document.getElementById('leaderboard-body');
        if (!tbody) return;
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;">Loading leaderboard...</td></tr>`;

        try {
            const res = await this.fetchApi('/api/leaderboard');
            if (res.success && res.data && res.data.length > 0) {
                tbody.innerHTML = res.data.map(item => {
                    let medal = item.rank;
                    if (item.rank === 1) medal = '🥇 1';
                    else if (item.rank === 2) medal = '🥈 2';
                    else if (item.rank === 3) medal = '🥉 3';

                    return `
                        <tr>
                            <td><strong>${medal}</strong></td>
                            <td><i class="fa-solid fa-user"></i> ${this.escapeHtml(item.username)}</td>
                            <td><strong style="color:var(--neon-amber);">${item.highestScore}</strong></td>
                            <td>${item.totalGamesPlayed}</td>
                            <td><span style="color:var(--primary-cyan);">${item.averageAccuracy}%</span></td>
                        </tr>
                    `;
                }).join('');
            } else {
                tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;">No player records found.</td></tr>`;
            }
        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--neon-ruby);">Failed to load leaderboard.</td></tr>`;
        }
    },

    async loadUserHistory() {
        const tbody = document.getElementById('history-body');
        if (!tbody) return;
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">Loading history...</td></tr>`;

        try {
            const res = await this.fetchApi('/api/users/me/history');
            if (res.success && res.data && res.data.games && res.data.games.length > 0) {
                tbody.innerHTML = res.data.games.map(g => {
                    const dateStr = g.startedAt ? new Date(g.startedAt).toLocaleString() : 'N/A';
                    return `
                        <tr>
                            <td>#${g.gameId}</td>
                            <td>${dateStr}</td>
                            <td><span class="badge badge-difficulty">${this.escapeHtml(g.difficulty)}</span></td>
                            <td><span class="badge badge-category">${this.escapeHtml(g.categoryName || 'All')}</span></td>
                            <td><strong style="color:var(--neon-amber);">${g.score}</strong></td>
                            <td>${g.accuracy}%</td>
                            <td><span class="${g.status === 'COMPLETED' ? 'tag-correct' : 'tag-wrong'}">${g.status}</span></td>
                        </tr>
                    `;
                }).join('');
            } else {
                tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">No previous game history found. Start a game!</td></tr>`;
            }
        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color:var(--neon-ruby);">Failed to load history.</td></tr>`;
        }
    },

    showToast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        let icon = 'fa-circle-info';
        if (type === 'success') icon = 'fa-circle-check';
        if (type === 'error') icon = 'fa-triangle-exclamation';

        toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${this.escapeHtml(message)}</span>`;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(50px)';
            setTimeout(() => toast.remove(), 300);
        }, 3500);
    },

    escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }
};

document.addEventListener('DOMContentLoaded', () => {
    app.init();
});
