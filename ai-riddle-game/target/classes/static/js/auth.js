/**
 * AI Riddle Arena - Authentication Controller
 */
const auth = {
    switchTab(tabName) {
        const loginBtn = document.getElementById('tab-login-btn');
        const regBtn = document.getElementById('tab-register-btn');
        const loginForm = document.getElementById('form-login');
        const regForm = document.getElementById('form-register');

        if (tabName === 'login') {
            loginBtn.classList.add('active');
            regBtn.classList.remove('active');
            loginForm.classList.remove('hidden');
            regForm.classList.add('hidden');
        } else {
            regBtn.classList.add('active');
            loginBtn.classList.remove('active');
            regForm.classList.remove('hidden');
            loginForm.classList.add('hidden');
        }
    },

    async handleLogin(event) {
        event.preventDefault();
        const usernameOrEmail = document.getElementById('login-username').value.trim();
        const password = document.getElementById('login-password').value.trim();

        if (!usernameOrEmail || !password) {
            app.showToast('Please enter both username and password', 'error');
            return;
        }

        try {
            const response = await app.fetchApi('/api/auth/login', {
                method: 'POST',
                body: { usernameOrEmail, password }
            });

            if (response.success && response.data) {
                const data = response.data;
                const user = {
                    id: data.id,
                    username: data.username,
                    email: data.email,
                    role: data.role
                };
                app.setSession(data.accessToken, user);
                app.showToast(`Welcome back, ${user.username}!`, 'success');
                app.navigateTo('dashboard');
            }
        } catch (err) {
            app.showToast(err.message || 'Login failed', 'error');
        }
    },

    async handleRegister(event) {
        event.preventDefault();
        const username = document.getElementById('reg-username').value.trim();
        const email = document.getElementById('reg-email').value.trim();
        const password = document.getElementById('reg-password').value.trim();

        if (!username || !email || !password) {
            app.showToast('Please fill in all required fields', 'error');
            return;
        }

        try {
            const response = await app.fetchApi('/api/auth/register', {
                method: 'POST',
                body: { username, email, password }
            });

            if (response.success && response.data) {
                const data = response.data;
                const user = {
                    id: data.id,
                    username: data.username,
                    email: data.email,
                    role: data.role
                };
                app.setSession(data.accessToken, user);
                app.showToast(`Account created! Welcome, ${user.username}!`, 'success');
                app.navigateTo('dashboard');
            }
        } catch (err) {
            app.showToast(err.message || 'Registration failed', 'error');
        }
    },

    async loadUserProfile() {
        if (!app.user) return;
        document.getElementById('dash-username').innerText = app.user.username;
        document.getElementById('dash-role').innerText = app.user.role === 'ROLE_ADMIN' ? 'ADMINISTRATOR' : 'PLAYER';

        try {
            const historyRes = await app.fetchApi('/api/users/me/history');
            if (historyRes.success && historyRes.data) {
                const data = historyRes.data;
                document.getElementById('dash-high-score').innerText = data.highestScore || 0;
                document.getElementById('dash-games-played').innerText = data.totalGamesPlayed || 0;
                document.getElementById('dash-accuracy').innerText = `${data.averageAccuracy || 0}%`;
            }
        } catch (e) {
            console.warn('Could not load user stats');
        }
    }
};
