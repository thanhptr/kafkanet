// k6 run --insecure-skip-tls-verify --summary-trend-stats "min,avg,max,med,p(75),p(90),p(95),p(99),p(99.99)" oe-profile-public.js
import { check } from 'k6';
import http from 'k6/http';

export default function() {
    let res = http.get("http://127.0.0.1:2000/api/test/sync?value=abc");
    check(res, {
        "OK": r => r.status === 200
    });
};