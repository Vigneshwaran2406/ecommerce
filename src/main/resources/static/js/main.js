// Main JavaScript file for the e-commerce application

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Product image hover effect
    const productCards = document.querySelectorAll('.product-card');
    if (productCards) {
        productCards.forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.classList.add('shadow');
            });
            
            card.addEventListener('mouseleave', function() {
                this.classList.remove('shadow');
            });
        });
    }
    
    // Quantity input handling for product detail page
    const quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        quantityInput.addEventListener('change', function() {
            if (parseInt(this.value) < 1) {
                this.value = 1;
            }
            
            const max = parseInt(this.getAttribute('max'));
            if (parseInt(this.value) > max) {
                this.value = max;
            }
        });
    }
    
    // Add fadeIn animation to cards
    const cards = document.querySelectorAll('.card');
    if (cards) {
        cards.forEach(card => {
            card.classList.add('fadeIn');
        });
    }
    
    // Handle form validation
    const forms = document.querySelectorAll('.needs-validation');
    if (forms) {
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                
                form.classList.add('was-validated');
            }, false);
        });
    }
    
    // Handle filter toggle on mobile
    const filterToggle = document.getElementById('filter-toggle');
    const filterContainer = document.getElementById('filter-container');
    
    if (filterToggle && filterContainer) {
        filterToggle.addEventListener('click', function() {
            filterContainer.classList.toggle('d-none');
        });
    }
});

// Function to confirm deletion
function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this item?');
}