/**
 * AI Riddle Arena - Admin Management Controller
 */
const admin = {
    riddles: [],
    categories: [],

    init() {
        this.loadCategories();
        this.loadRiddles();
    },

    switchTab(tabName) {
        const rBtn = document.getElementById('admin-riddles-tab');
        const cBtn = document.getElementById('admin-categories-tab');
        const rSec = document.getElementById('admin-riddles-section');
        const cSec = document.getElementById('admin-categories-section');

        if (tabName === 'riddles') {
            rBtn.classList.add('active');
            cBtn.classList.remove('active');
            rSec.classList.remove('hidden');
            cSec.classList.add('hidden');
        } else {
            cBtn.classList.add('active');
            rBtn.classList.remove('active');
            cSec.classList.remove('hidden');
            rSec.classList.add('hidden');
        }
    },

    async loadCategories() {
        try {
            const res = await app.fetchApi('/api/admin/categories');
            if (res.success && res.data) {
                this.categories = res.data;
                this.renderCategoriesTable();
                this.populateCategoryDropdown();
            }
        } catch (err) {
            console.warn('Admin category fetch failed', err);
        }
    },

    async loadRiddles() {
        try {
            const res = await app.fetchApi('/api/riddles');
            if (res.success && res.data) {
                this.riddles = res.data;
                this.renderRiddlesTable();
            }
        } catch (err) {
            console.warn('Admin riddles fetch failed', err);
        }
    },

    renderRiddlesTable() {
        const tbody = document.getElementById('admin-riddles-body');
        if (!tbody) return;

        if (this.riddles.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">No riddles in question bank.</td></tr>`;
            return;
        }

        tbody.innerHTML = this.riddles.map(r => `
            <tr>
                <td>#${r.id}</td>
                <td style="max-width:300px;">${app.escapeHtml(r.question)}</td>
                <td><span class="badge badge-category">${app.escapeHtml(r.categoryName)}</span></td>
                <td><span class="badge badge-difficulty">${app.escapeHtml(r.difficulty)}</span></td>
                <td><strong>${app.escapeHtml(r.correctAnswer)}</strong></td>
                <td>${r.basePoints}</td>
                <td>
                    <button class="btn btn-secondary" style="padding:0.4rem 0.8rem; font-size:0.8rem;" onclick="admin.editRiddle(${r.id})"><i class="fa-solid fa-pen"></i></button>
                    <button class="btn btn-secondary" style="padding:0.4rem 0.8rem; font-size:0.8rem; color:var(--neon-ruby);" onclick="admin.deleteRiddle(${r.id})"><i class="fa-solid fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    },

    renderCategoriesTable() {
        const tbody = document.getElementById('admin-categories-body');
        if (!tbody) return;

        if (this.categories.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;">No categories found.</td></tr>`;
            return;
        }

        tbody.innerHTML = this.categories.map(c => `
            <tr>
                <td>#${c.id}</td>
                <td><strong>${app.escapeHtml(c.name)}</strong></td>
                <td>${app.escapeHtml(c.description || 'N/A')}</td>
                <td>
                    <button class="btn btn-secondary" style="padding:0.4rem 0.8rem; font-size:0.8rem;" onclick="admin.editCategory(${c.id})"><i class="fa-solid fa-pen"></i></button>
                    <button class="btn btn-secondary" style="padding:0.4rem 0.8rem; font-size:0.8rem; color:var(--neon-ruby);" onclick="admin.deleteCategory(${c.id})"><i class="fa-solid fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    },

    populateCategoryDropdown() {
        const select = document.getElementById('riddle-category');
        if (!select) return;

        select.innerHTML = this.categories.map(c => `
            <option value="${c.id}">${app.escapeHtml(c.name)}</option>
        `).join('');
    },

    openRiddleModal(riddleData = null) {
        document.getElementById('riddle-modal-title').innerText = riddleData ? 'Edit Riddle' : 'Add New Riddle';
        document.getElementById('riddle-id').value = riddleData ? riddleData.id : '';
        document.getElementById('riddle-question').value = riddleData ? riddleData.question : '';
        document.getElementById('riddle-correct-answer').value = riddleData ? riddleData.correctAnswer : '';
        document.getElementById('riddle-options').value = (riddleData && riddleData.options) ? riddleData.options.join(', ') : '';
        document.getElementById('riddle-alt-answers').value = (riddleData && riddleData.altAnswers) ? riddleData.altAnswers.join(', ') : '';
        document.getElementById('riddle-hint').value = riddleData ? riddleData.hint : '';
        document.getElementById('riddle-difficulty').value = riddleData ? riddleData.difficulty : 'Easy';
        document.getElementById('riddle-points').value = riddleData ? riddleData.basePoints : 100;

        if (riddleData && riddleData.categoryId) {
            document.getElementById('riddle-category').value = riddleData.categoryId;
        }

        document.getElementById('riddle-modal').classList.remove('hidden');
    },

    closeRiddleModal() {
        document.getElementById('riddle-modal').classList.add('hidden');
    },

    async saveRiddle(event) {
        event.preventDefault();
        const id = document.getElementById('riddle-id').value;
        const question = document.getElementById('riddle-question').value.trim();
        const categoryId = parseInt(document.getElementById('riddle-category').value);
        const difficulty = document.getElementById('riddle-difficulty').value;
        const correctAnswer = document.getElementById('riddle-correct-answer').value.trim();
        const optionsRaw = document.getElementById('riddle-options').value.trim();
        const altAnswersRaw = document.getElementById('riddle-alt-answers').value.trim();
        const hint = document.getElementById('riddle-hint').value.trim();
        const basePoints = parseInt(document.getElementById('riddle-points').value);

        const options = optionsRaw ? optionsRaw.split(',').map(s => s.trim()).filter(Boolean) : [];
        const altAnswers = altAnswersRaw ? altAnswersRaw.split(',').map(s => s.trim()).filter(Boolean) : [];

        const payload = {
            question, categoryId, difficulty, correctAnswer, options, altAnswers, hint, basePoints
        };

        try {
            let res;
            if (id) {
                res = await app.fetchApi(`/api/admin/riddles/${id}`, { method: 'PUT', body: payload });
            } else {
                res = await app.fetchApi('/api/admin/riddles', { method: 'POST', body: payload });
            }

            if (res.success) {
                app.showToast(id ? 'Riddle updated!' : 'Riddle created!', 'success');
                this.closeRiddleModal();
                this.loadRiddles();
            }
        } catch (err) {
            app.showToast(err.message || 'Save riddle failed', 'error');
        }
    },

    editRiddle(id) {
        const riddle = this.riddles.find(r => r.id === id);
        if (riddle) this.openRiddleModal(riddle);
    },

    async deleteRiddle(id) {
        if (!confirm('Are you sure you want to delete this riddle?')) return;
        try {
            const res = await app.fetchApi(`/api/admin/riddles/${id}`, { method: 'DELETE' });
            if (res.success) {
                app.showToast('Riddle deleted successfully', 'success');
                this.loadRiddles();
            }
        } catch (err) {
            app.showToast(err.message || 'Delete failed', 'error');
        }
    },

    openCategoryModal(catData = null) {
        document.getElementById('category-modal-title').innerText = catData ? 'Edit Category' : 'Add Category';
        document.getElementById('category-id').value = catData ? catData.id : '';
        document.getElementById('category-name').value = catData ? catData.name : '';
        document.getElementById('category-desc').value = catData ? catData.description : '';
        document.getElementById('category-modal').classList.remove('hidden');
    },

    closeCategoryModal() {
        document.getElementById('category-modal').classList.add('hidden');
    },

    async saveCategory(event) {
        event.preventDefault();
        const id = document.getElementById('category-id').value;
        const name = document.getElementById('category-name').value.trim();
        const description = document.getElementById('category-desc').value.trim();

        const payload = { name, description };

        try {
            let res;
            if (id) {
                res = await app.fetchApi(`/api/admin/categories/${id}`, { method: 'PUT', body: payload });
            } else {
                res = await app.fetchApi('/api/admin/categories', { method: 'POST', body: payload });
            }

            if (res.success) {
                app.showToast(id ? 'Category updated!' : 'Category created!', 'success');
                this.closeCategoryModal();
                this.loadCategories();
            }
        } catch (err) {
            app.showToast(err.message || 'Save category failed', 'error');
        }
    },

    editCategory(id) {
        const cat = this.categories.find(c => c.id === id);
        if (cat) this.openCategoryModal(cat);
    },

    async deleteCategory(id) {
        if (!confirm('Are you sure you want to delete this category?')) return;
        try {
            const res = await app.fetchApi(`/api/admin/categories/${id}`, { method: 'DELETE' });
            if (res.success) {
                app.showToast('Category deleted', 'success');
                this.loadCategories();
            }
        } catch (err) {
            app.showToast(err.message || 'Delete failed', 'error');
        }
    }
};
