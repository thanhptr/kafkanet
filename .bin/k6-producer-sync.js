// k6 run --insecure-skip-tls-verify --summary-trend-stats "min,avg,max,med,p(75),p(90),p(95),p(99),p(99.99)" oe-profile-public.js
import { check } from 'k6';
import http from 'k6/http';

export let options = {
  stages: [
    // // VERY HIGH
    // { target: 3, duration: "10s" },
    // { target: 10, duration: "10s" },
    // { target: 10, duration: "20s" },
    // { target: 0, duration: "30s" },

    // // HIGH
    // { target: 3, duration: "10s" },
    // { target: 6, duration: "10s" },
    // { target: 6, duration: "20s" },
    // { target: 0, duration: "20s" },

    // // A LITLE HIGH
    // { target: 4, duration: "10s" },
    // { target: 0, duration: "10s" },

    // NORMAL
    { target: 3, duration: "10s" },
    { target: 0, duration: "10s" },
  ]
};

export default function() {
    let res = http.get("http://127.0.0.1:2000/api/test/sync?value=");
    check(res, {
        "OK": r => r.status === 200
    });
};