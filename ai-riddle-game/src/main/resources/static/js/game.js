/**
 * AI Riddle Arena - Riddle Game Controller
 */
const game = {
    selectedDifficulty: 'Medium',
    selectedCategory: 'All',
    activeGameId: null,
    currentQuestion: null,
    timerInterval: null,
    timeLeft: 30,
    startTime: null,
    selectedOption: null,

    async loadCategories() {
        const grid = document.getElementById('category-picker-grid');
        if (!grid) return;

        try {
            const res = await app.fetchApi('/api/riddles/public');
            let categories = ['All'];
            if (res.success && res.data) {
                const fetchedCats = [...new Set(res.data.map(r => r.categoryName))];
                categories = ['All', ...fetchedCats];
            }

            grid.innerHTML = categories.map(cat => `
                <button class="cat-btn ${this.selectedCategory === cat ? 'active' : ''}" onclick="game.setCategory('${app.escapeHtml(cat)}')">
                    <i class="fa-solid ${cat === 'All' ? 'fa-globe' : 'fa-tag'}"></i> ${app.escapeHtml(cat)}
                </button>
            `).join('');
        } catch (e) {
            console.warn('Using default categories');
        }
    },

    setDifficulty(diff) {
        this.selectedDifficulty = diff;
        document.querySelectorAll('.diff-card').forEach(card => {
            if (card.dataset.diff === diff) {
                card.classList.add('active');
            } else {
                card.classList.remove('active');
            }
        });
    },

    setCategory(cat) {
        this.selectedCategory = cat;
        document.querySelectorAll('.cat-btn').forEach(btn => {
            if (btn.innerText.trim().endsWith(cat)) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
    },

    async initiateGame() {
        try {
            app.showToast('AI Engine initializing question bank...', 'info');
            const res = await app.fetchApi('/api/game/start', {
                method: 'POST',
                body: {
                    difficulty: this.selectedDifficulty,
                    categoryName: this.selectedCategory,
                    totalQuestions: 5
                }
            });

            if (res.success && res.data) {
                this.activeGameId = res.data.gameId;
                document.getElementById('arena-diff-badge').innerText = res.data.difficulty.toUpperCase();
                document.getElementById('arena-cat-badge').innerText = (res.data.categoryName || 'ALL').toUpperCase();
                document.getElementById('arena-total-q').innerText = res.data.totalQuestions;
                document.getElementById('arena-score').innerText = '0';
                
                app.navigateTo('game');
                this.fetchQuestion();
            }
        } catch (err) {
            app.showToast(err.message || 'Failed to start game session', 'error');
        }
    },

    async fetchQuestion() {
        if (!this.activeGameId) return;
        this.resetQuestionState();

        try {
            const res = await app.fetchApi(`/api/game/${this.activeGameId}/question`);
            if (res.success && res.data) {
                this.currentQuestion = res.data;
                this.renderQuestion(res.data);
                this.startTimer(res.data.timerSeconds || 30);
            }
        } catch (err) {
            app.showToast(err.message || 'Failed to load question', 'error');
        }
    },

    renderQuestion(qData) {
        document.getElementById('arena-current-q').innerText = qData.questionOrder;
        document.getElementById('arena-question-text').innerText = qData.question;

        // Hint container reset
        const hintContainer = document.getElementById('hint-container');
        if (qData.hintUsed && qData.hintText) {
            hintContainer.classList.remove('hidden');
            document.getElementById('hint-text-display').innerText = qData.hintText;
        } else {
            hintContainer.classList.add('hidden');
        }

        const optionsWrapper = document.getElementById('answer-options-wrapper');
        const textAnswerBox = document.getElementById('text-answer-box');

        if (qData.options && qData.options.length > 0) {
            optionsWrapper.classList.remove('hidden');
            textAnswerBox.classList.add('hidden');

            optionsWrapper.innerHTML = qData.options.map((opt, idx) => {
                const letter = String.fromCharCode(65 + idx);
                return `
                    <div class="option-card" onclick="game.selectOption('${letter}', this)">
                        <div class="option-letter">${letter}</div>
                        <span>${app.escapeHtml(opt)}</span>
                    </div>
                `;
            }).join('');
        } else {
            // Text answer fallback
            optionsWrapper.classList.add('hidden');
            textAnswerBox.classList.remove('hidden');
            document.getElementById('text-answer-input').value = '';
        }
    },

    selectOption(letter, element) {
        this.selectedOption = letter;
        document.querySelectorAll('.option-card').forEach(card => card.classList.remove('selected'));
        element.classList.add('selected');
    },

    resetQuestionState() {
        clearInterval(this.timerInterval);
        this.selectedOption = null;
        this.currentQuestion = null;
        this.timeLeft = 30;
        this.startTime = Date.now();
    },

    startTimer(seconds) {
        this.timeLeft = seconds;
        const timerText = document.getElementById('timer-text');
        const timerBar = document.getElementById('timer-bar');
        const maxDash = 264; // SVG stroke circumference (2 * pi * 42)

        timerText.innerText = this.timeLeft;
        timerBar.style.strokeDashoffset = '0';
        timerBar.style.stroke = 'var(--primary-cyan)';

        this.timerInterval = setInterval(() => {
            this.timeLeft--;
            timerText.innerText = this.timeLeft;

            const offset = maxDash - (this.timeLeft / seconds) * maxDash;
            timerBar.style.strokeDashoffset = offset;

            if (this.timeLeft <= 10) {
                timerBar.style.stroke = 'var(--neon-ruby)';
            } else if (this.timeLeft <= 18) {
                timerBar.style.stroke = 'var(--neon-amber)';
            }

            if (this.timeLeft <= 0) {
                clearInterval(this.timerInterval);
                app.showToast('Time expired! Submitting timeout...', 'error');
                this.submitAnswer(true); // timeout submission
            }
        }, 1000);
    },

    async requestHint() {
        if (!this.activeGameId) return;
        try {
            const res = await app.fetchApi(`/api/game/${this.activeGameId}/hint`);
            if (res.success && res.data) {
                document.getElementById('hint-container').classList.remove('hidden');
                document.getElementById('hint-text-display').innerText = res.data.hint;
                app.showToast('Hint revealed! (-50% score penalty applied)', 'info');
            }
        } catch (err) {
            app.showToast(err.message || 'Could not fetch hint', 'error');
        }
    },

    async submitAnswer(isTimeout = false) {
        if (!this.activeGameId) return;
        clearInterval(this.timerInterval);

        let answer = '';
        if (isTimeout) {
            answer = 'TIMEOUT_NO_ANSWER';
        } else if (this.currentQuestion && this.currentQuestion.options && this.currentQuestion.options.length > 0) {
            if (!this.selectedOption) {
                app.showToast('Please select an option before submitting!', 'error');
                this.startTimer(this.timeLeft); // resume
                return;
            }
            answer = this.selectedOption;
        } else {
            const inputVal = document.getElementById('text-answer-input').value.trim();
            if (!inputVal) {
                app.showToast('Please type an answer!', 'error');
                this.startTimer(this.timeLeft); // resume
                return;
            }
            answer = inputVal;
        }

        const elapsedSeconds = Math.round((Date.now() - this.startTime) / 1000);

        try {
            const res = await app.fetchApi(`/api/game/${this.activeGameId}/answer`, {
                method: 'POST',
                body: {
                    answer: answer,
                    responseTimeSeconds: elapsedSeconds
                }
            });

            if (res.success && res.data) {
                const data = res.data;
                document.getElementById('arena-score').innerText = data.totalScore;

                if (data.isCorrect) {
                    app.showToast(`+${data.pointsAwarded} PTS! ${data.explanation}`, 'success');
                } else {
                    app.showToast(`Incorrect! Answer was: "${data.correctAnswer}"`, 'error');
                }

                if (data.isGameOver) {
                    setTimeout(() => this.showResults(), 1200);
                } else {
                    setTimeout(() => this.fetchQuestion(), 1200);
                }
            }
        } catch (err) {
            app.showToast(err.message || 'Answer submission failed', 'error');
        }
    },

    async showResults() {
        if (!this.activeGameId) return;

        try {
            const res = await app.fetchApi(`/api/game/${this.activeGameId}/result`);
            if (res.success && res.data) {
                const r = res.data;
                document.getElementById('res-rank-badge').innerText = r.performanceRank;
                document.getElementById('res-total-score').innerText = r.totalScore;
                document.getElementById('res-accuracy').innerText = `${r.accuracy}%`;
                document.getElementById('res-correct-count').innerText = `${r.correctCount}/${r.totalQuestions}`;

                const tbody = document.getElementById('res-breakdown-body');
                tbody.innerHTML = r.questionReviews.map(q => `
                    <tr>
                        <td>#${q.questionOrder}</td>
                        <td>${app.escapeHtml(q.question)}</td>
                        <td><span class="${q.isCorrect ? 'tag-correct' : 'tag-wrong'}">${app.escapeHtml(q.userAnswer || 'None')}</span></td>
                        <td><strong>${app.escapeHtml(q.correctAnswer)}</strong></td>
                        <td>${q.hintUsed ? '⚡ Used (-50%)' : 'None'}</td>
                        <td><strong style="color:var(--neon-amber);">+${q.pointsAwarded}</strong></td>
                    </tr>
                `).join('');

                app.navigateTo('results');
            }
        } catch (err) {
            app.showToast(err.message || 'Failed to load results summary', 'error');
        }
    }
};
