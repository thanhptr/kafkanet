k6 run --insecure-skip-tls-verify --summary-trend-stats "min,avg,max,med,p(75),p(90),p(95),p(99),p(99.99)" k6-backend.js # -u 3 -d 10s 