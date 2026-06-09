/**
 * ReparValo - Core Frontend Application Logic
 * 
 * Handles interactive tabs, theme toggling, REST API integration,
 * SVG path selections, and AI endpoints powered by Gemini.
 * 
 * Localized for the Swiss market (Geneva, CHF, 150 CHF/h labor rate).
 */

document.addEventListener('DOMContentLoaded', () => {
    // State Variables
    let vehicleCatalog = [];

    // French translation dictionary for database part names
    const PART_TRANSLATIONS = {
        'rear_right_door': 'Porte arrière droite',
        'front_right_door': 'Porte avant droite',
        'rear_left_door': 'Porte arrière gauche',
        'front_left_door': 'Porte avant gauche',
        'front_bumper': 'Pare-chocs avant',
        'rear_bumper': 'Pare-chocs arrière',
        'left_headlight': 'Phare gauche',
        'right_headlight': 'Phare droit',
        'left_mirror': 'Rétroviseur gauche',
        'right_mirror': 'Rétroviseur droit',
        'hood': 'Capot',
        'trunk': 'Coffre / Hayon'
    };

    // Initialize UI Elements
    initTheme();
    initTabs();
    fetchVehicles();
    initTradeInForm();
    initSvgInteractions();
    initRepairOptions();
    initAiAnalysis();

    // ====================================================================
    // 1. Helper Functions
    // ====================================================================

    /**
     * Formats a numeric value into Swiss Francs (CHF) representation.
     */
    function formatCHF(value) {
        if (value === undefined || value === null) return '0.00 CHF';
        return new Intl.NumberFormat('fr-CH', {
            style: 'currency',
            currency: 'CHF',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(value);
    }

    /**
     * Basic markdown parsing utility to render AI Gemini reports cleanly in HTML.
     */
    function parseMarkdown(md) {
        if (!md) return '';
        
        // Escape HTML tags to protect against XSS
        let html = md
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');

        const lines = html.split('\n');
        let inList = false;
        let result = [];

        for (let line of lines) {
            let trimmed = line.trim();
            
            // Headings
            if (trimmed.startsWith('### ')) {
                if (inList) { result.push('</ul>'); inList = false; }
                result.push(`<h3>${trimmed.substring(4)}</h3>`);
            } else if (trimmed.startsWith('## ')) {
                if (inList) { result.push('</ul>'); inList = false; }
                result.push(`<h2>${trimmed.substring(3)}</h2>`);
            } else if (trimmed.startsWith('# ')) {
                if (inList) { result.push('</ul>'); inList = false; }
                result.push(`<h1>${trimmed.substring(2)}</h1>`);
            } 
            // Bullet list items
            else if (trimmed.startsWith('- ') || trimmed.startsWith('* ')) {
                if (!inList) { result.push('<ul>'); inList = true; }
                let content = trimmed.substring(2);
                content = content.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
                result.push(`<li>${content}</li>`);
            } 
            // Empty line
            else if (trimmed === '') {
                if (inList) { result.push('</ul>'); inList = false; }
            } 
            // Normal paragraph
            else {
                if (inList) { result.push('</ul>'); inList = false; }
                let content = trimmed.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
                result.push(`<p>${content}</p>`);
            }
        }
        
        if (inList) { result.push('</ul>'); }
        return result.join('\n');
    }

    // ====================================================================
    // 2. Theme Initialization & Control
    // ====================================================================
    function initTheme() {
        const themeToggleBtn = document.getElementById('theme-toggle');
        const body = document.body;

        // Load theme from LocalStorage or default to dark-theme
        const savedTheme = localStorage.getItem('theme') || 'dark';
        if (savedTheme === 'light') {
            body.classList.replace('dark-theme', 'light-theme');
        } else {
            body.classList.replace('light-theme', 'dark-theme');
        }

        themeToggleBtn.addEventListener('click', () => {
            if (body.classList.contains('dark-theme')) {
                body.classList.replace('dark-theme', 'light-theme');
                localStorage.setItem('theme', 'light');
            } else {
                body.classList.replace('light-theme', 'dark-theme');
                localStorage.setItem('theme', 'dark');
            }
        });
    }

    // ====================================================================
    // 3. Tab Navigation
    // ====================================================================
    function initTabs() {
        const tabs = document.querySelectorAll('.tab-btn');
        const tabContents = document.querySelectorAll('.tab-content');

        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                // Remove active classes
                tabs.forEach(t => t.classList.remove('active'));
                tabContents.forEach(c => c.classList.remove('active'));

                // Set active tab and content
                tab.classList.add('active');
                const targetTabId = tab.getAttribute('data-tab');
                document.getElementById(targetTabId).classList.add('active');
            });
        });
    }

    // ====================================================================
    // 4. Vehicle Catalog Data Loading
    // ====================================================================
    function fetchVehicles() {
        const carSelect = document.getElementById('car-select');

        fetch('/api/vehicles')
            .then(res => {
                if (!res.ok) throw new Error('Impossible de charger le catalogue.');
                return res.json();
            })
            .then(data => {
                vehicleCatalog = data;
                
                // Clear loading option
                carSelect.innerHTML = '<option value="">-- Choisir un modèle --</option>';
                
                // Populate options
                data.forEach((v, index) => {
                    const option = document.createElement('option');
                    option.value = index; // Store catalog array index
                    option.textContent = `${v.make} ${v.model} (Neuf: ${formatCHF(v.baseValueChf)})`;
                    carSelect.appendChild(option);
                });
            })
            .catch(err => {
                console.error(err);
                carSelect.innerHTML = '<option value="">Erreur de chargement du catalogue</option>';
            });
    }

    // ====================================================================
    // 5. Dealer Trade-In Form & Valuation Results
    // ====================================================================
    function initTradeInForm() {
        const form = document.getElementById('trade-in-form');
        const resultCard = document.getElementById('trade-in-result-card');
        const emptyState = resultCard.querySelector('.empty-state');
        const resultCont = document.getElementById('trade-in-result-content');

        const finalOfferEl = document.getElementById('result-final-offer');
        const baseValEl = document.getElementById('result-base-value');
        const depValEl = document.getElementById('result-depreciated-value');
        const mileageAdjEl = document.getElementById('result-mileage-adj');
        const penaltyEl = document.getElementById('result-condition-penalty');
        const marginEl = document.getElementById('result-dealer-margin');
        const aiReportText = document.getElementById('ai-report-text');

        form.addEventListener('submit', (e) => {
            e.preventDefault();

            const selectedIndex = document.getElementById('car-select').value;
            const year = parseInt(document.getElementById('car-year').value);
            const mileage = parseFloat(document.getElementById('car-mileage').value);
            const condition = document.getElementById('car-condition').value;
            const preferFrench = document.getElementById('prefer-french-report').checked;

            if (selectedIndex === "") return;

            const vehicle = vehicleCatalog[selectedIndex];

            const requestBody = {
                make: vehicle.make,
                model: vehicle.model,
                year: year,
                mileage: mileage,
                condition: condition
            };

            // Switch result view states
            if (emptyState) emptyState.classList.add('hidden');
            resultCont.classList.remove('hidden');

            // Set loading state for the AI section
            aiReportText.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Génération du rapport de reprise par l\'IA...';

            // 1. Submit trade-in estimate calculation
            fetch('/api/estimate/trade-in', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestBody)
            })
            .then(res => {
                if (!res.ok) throw new Error('Erreur lors du calcul de la reprise.');
                return res.json();
            })
            .then(estimation => {
                // Render numerical breakdown
                finalOfferEl.textContent = formatCHF(estimation.finalOffer);
                baseValEl.textContent = formatCHF(estimation.baseValueChf);
                depValEl.textContent = formatCHF(estimation.depreciatedValue);
                
                // Colorize adjustments
                mileageAdjEl.textContent = (estimation.mileageAdjustment >= 0 ? '+' : '') + formatCHF(estimation.mileageAdjustment);
                mileageAdjEl.className = estimation.mileageAdjustment >= 0 ? 'val text-success' : 'val text-danger';

                penaltyEl.textContent = '-' + formatCHF(Math.abs(estimation.conditionPenalty));
                marginEl.textContent = '-' + formatCHF(estimation.dealerMargin);

                // 2. Query AI endpoint to obtain professional synthesis pitch
                return fetch('/api/ai/report', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        estimation: estimation,
                        preferFrench: preferFrench
                    })
                });
            })
            .then(res => {
                if (!res.ok) throw new Error('Erreur de communication avec l\'IA.');
                return res.json();
            })
            .then(aiRes => {
                // Render markdown content
                aiReportText.innerHTML = `<div class="ai-report-content">${parseMarkdown(aiRes.content)}</div>`;
            })
            .catch(err => {
                console.error(err);
                aiReportText.innerHTML = `<p class="text-danger"><i class="fa-solid fa-circle-exclamation"></i> Échec de génération du rapport : ${err.message}</p>`;
            });
        });
    }

    // ====================================================================
    // 6. Interactive SVG Car Schematic
    // ====================================================================
    function initSvgInteractions() {
        const parts = document.querySelectorAll('.car-part');

        parts.forEach(part => {
            // Set dynamic title attributes if available
            const partId = part.getAttribute('id');
            if (PART_TRANSLATIONS[partId]) {
                part.setAttribute('title', PART_TRANSLATIONS[partId]);
            }

            // Click listener
            part.addEventListener('click', () => {
                part.classList.toggle('damaged');
                updateRepairEstimate();
            });
        });
    }

    // ====================================================================
    // 7. Repair Options (Used/Recycled components toggle)
    // ====================================================================
    function initRepairOptions() {
        const checkbox = document.getElementById('use-used-parts');
        checkbox.addEventListener('change', () => {
            updateRepairEstimate();
        });
    }

    /**
     * Re-calculates and renders the detailed repair estimate table
     * using the current damaged parts selection and used parts preference.
     */
    function updateRepairEstimate() {
        const damagedPaths = document.querySelectorAll('.car-part.damaged');
        const useUsedParts = document.getElementById('use-used-parts').checked;
        const tableBody = document.getElementById('repair-items-body');

        const totalPartsCostEl = document.getElementById('total-parts-cost');
        const totalLaborCostEl = document.getElementById('total-labor-cost');
        const grandTotalEl = document.getElementById('grand-total-repair-cost');

        // Extract list of selected part identifiers
        const selectedPartIds = [];
        damagedPaths.forEach(path => {
            selectedPartIds.push(path.getAttribute('id'));
        });

        // If no parts are damaged, reset table and values
        if (selectedPartIds.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-table">Aucune pièce sélectionnée. Cliquez sur le schéma pour déclarer un dégât.</td>
                </tr>
            `;
            totalPartsCostEl.textContent = '0.00 CHF';
            totalLaborCostEl.textContent = '0.00 CHF';
            grandTotalEl.textContent = '0.00 CHF';
            return;
        }

        // Call backend Repair Estimation service
        fetch('/api/estimate/repair', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                partNames: selectedPartIds,
                useUsedParts: useUsedParts
            })
        })
        .then(res => {
            if (!res.ok) throw new Error('Impossible de calculer le devis.');
            return res.json();
        })
        .then(estimate => {
            // Clear table
            tableBody.innerHTML = '';

            // Populate rows
            estimate.details.forEach(item => {
                const tr = document.createElement('tr');
                
                // Translated Name and Swiss Badges
                const translatedName = PART_TRANSLATIONS[item.partName] || item.partName;
                
                // Construct badges
                const searchQ = encodeURIComponent(translatedName);
                const badgesHTML = `
                    <div class="sourcing-badges">
                        <a href="https://www.mister-auto.ch/fr/recherche/?q=${searchQ}" target="_blank" class="badge-link new-part" title="Acheter neuf sur Mister-Auto">
                            <i class="fa-solid fa-cart-shopping"></i> Mister-Auto.ch
                        </a>
                        <a href="https://www.ricardo.ch/fr/s/${searchQ}" target="_blank" class="badge-link used-part" title="Rechercher d'occasion sur Ricardo">
                            <i class="fa-solid fa-magnifying-glass"></i> Ricardo.ch
                        </a>
                        <a href="https://www.tutti.ch/fr/liens?q=${searchQ}" target="_blank" class="badge-link used-part" title="Rechercher d'occasion sur tutti.ch">
                            <i class="fa-solid fa-magnifying-glass"></i> tutti.ch
                        </a>
                        <a href="https://www.anibis.ch/fr/s/?q=${searchQ}" target="_blank" class="badge-link used-part" title="Rechercher d'occasion sur anibis.ch">
                            <i class="fa-solid fa-magnifying-glass"></i> anibis.ch
                        </a>
                    </div>
                `;

                tr.innerHTML = `
                    <td>
                        <div class="repair-part-name">${translatedName}</div>
                        ${badgesHTML}
                    </td>
                    <td>${formatCHF(item.partPriceChf)}</td>
                    <td>${item.laborHours.toFixed(1)} h</td>
                    <td>${formatCHF(item.laborCostChf)}</td>
                    <td><strong>${formatCHF(item.totalPartCostChf)}</strong></td>
                `;
                tableBody.appendChild(tr);
            });

            // Update footer totals
            totalPartsCostEl.textContent = formatCHF(estimate.totalPartsCostChf);
            totalLaborCostEl.textContent = formatCHF(estimate.totalLaborCostChf);
            grandTotalEl.textContent = formatCHF(estimate.totalRepairCostChf);
        })
        .catch(err => {
            console.error(err);
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-table text-danger">
                        <i class="fa-solid fa-circle-exclamation"></i> Échec du calcul : ${err.message}
                    </td>
                </tr>
            `;
        });
    }

    // ====================================================================
    // 8. AI Damage Analysis via Free Text
    // ====================================================================
    function initAiAnalysis() {
        const analyzeBtn = document.getElementById('btn-analyze-damages');
        const descTextarea = document.getElementById('ai-damage-description');
        const inputGroup = document.querySelector('.ai-input-group');

        analyzeBtn.addEventListener('click', () => {
            const text = descTextarea.value.trim();
            if (!text) {
                alert('Veuillez décrire le sinistre ou les dégâts observés avant de lancer l\'analyse.');
                return;
            }

            // Set loading button state
            const originalHTML = analyzeBtn.innerHTML;
            analyzeBtn.disabled = true;
            analyzeBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Analyse IA en cours...';

            // Clear previous explanation box if it exists
            const prevBox = document.getElementById('ai-explanation-box');
            if (prevBox) prevBox.remove();

            fetch('/api/ai/analyze-text', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ text: text })
            })
            .then(res => {
                if (!res.ok) throw new Error('Impossible d\'extraire les dégâts.');
                return res.json();
            })
            .then(extraction => {
                // Clear current selections
                const allParts = document.querySelectorAll('.car-part');
                allParts.forEach(p => p.classList.remove('damaged'));

                // Apply new selections from AI analysis
                if (extraction.damagedParts && extraction.damagedParts.length > 0) {
                    extraction.damagedParts.forEach(partId => {
                        const pathEl = document.getElementById(partId);
                        if (pathEl) {
                            pathEl.classList.add('damaged');
                        }
                    });
                }

                // Trigger UI update
                updateRepairEstimate();

                // Append explanation details card
                const explanationBox = document.createElement('div');
                explanationBox.id = 'ai-explanation-box';
                explanationBox.className = 'ai-report-section';
                explanationBox.style.marginTop = '15px';
                explanationBox.innerHTML = `
                    <h4><i class="fa-solid fa-lightbulb"></i> Analyse de l'IA (Gemini)</h4>
                    <p style="font-size: 13.5px; line-height: 1.5; color: var(--text-secondary);">${extraction.explanation}</p>
                `;
                inputGroup.appendChild(explanationBox);
            })
            .catch(err => {
                console.error(err);
                alert(`Erreur d'analyse par l'IA : ${err.message}`);
            })
            .finally(() => {
                // Restore button state
                analyzeBtn.disabled = false;
                analyzeBtn.innerHTML = originalHTML;
            });
        });
    }
});
