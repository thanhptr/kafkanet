// k6 run --insecure-skip-tls-verify --summary-trend-stats "min,avg,max,med,p(75),p(90),p(95),p(99),p(99.99)" oe-profile-public.js
import { check } from 'k6';
import http from 'k6/http';

var USERS = 10;
let RAMP_UP = "3s";
let RUNNING = "60s";
let RAMP_DOWN = "60s";
 
export let options = {
  stages: [
    { target: USERS, duration: RAMP_UP },
    { target: USERS, duration: RUNNING },
    { target: 0, duration: RAMP_DOWN },
  ]
};

export default function() {
    let res = http.get("http://127.0.0.1:2000/api/test/async?value=abc");
    check(res, {
        "OK": r => r.status === 200
    });
};