const $ = (s) => document.querySelector(s);
let currentUser = null;
const safe = (v) => String(v ?? "").replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
async function api(url, options={}) {
  const response = await fetch(url, {credentials:"same-origin", headers:{"Content-Type":"application/json", ...(options.headers||{})}, ...options});
  const data = response.status === 204 ? null : await response.json().catch(()=>null);
  if (!response.ok) throw new Error(data?.message || `Request failed (${response.status})`);
  return data;
}
function showMessage(selector, message, good=false) { const el=$(selector); el.textContent=message; el.style.color=good?"#16643a":"#a13c32"; }
function authTab(tab) {
  $("#loginForm").classList.toggle("hidden", tab!=="login");
  $("#registerForm").classList.toggle("hidden", tab!=="register");
  document.querySelectorAll(".tab").forEach(b=>b.classList.toggle("active",b.dataset.tab===tab));
  showMessage("#authMessage","");
}
document.querySelectorAll(".tab").forEach(b=>b.addEventListener("click",()=>authTab(b.dataset.tab)));
$("#registerForm").addEventListener("submit", async e=>{
  e.preventDefault(); const f=new FormData(e.target);
  try { const result=await api("/api/auth/register",{method:"POST",body:JSON.stringify(Object.fromEntries(f))}); showMessage("#authMessage",result.message,true); e.target.reset(); authTab("login"); }
  catch(err){showMessage("#authMessage",err.message);}
});
$("#loginForm").addEventListener("submit", async e=>{
  e.preventDefault(); const f=new FormData(e.target);
  try { currentUser=await api("/api/auth/login",{method:"POST",body:JSON.stringify(Object.fromEntries(f))}); await showApp(); }
  catch(err){showMessage("#authMessage",err.message);}
});
$("#logoutBtn").addEventListener("click",async()=>{try{await api("/api/auth/logout",{method:"POST",body:"{}"});}catch(_){} currentUser=null; showAuth();});
$("#reportForm").addEventListener("submit",async e=>{
  e.preventDefault(); const f=Object.fromEntries(new FormData(e.target));
  f.dateTime = f.dateTime ? f.dateTime + ":00" : "";
  try { await api("/api/reports",{method:"POST",body:JSON.stringify(f)}); e.target.reset(); showMessage("#reportMessage","Report submitted successfully.",true); await refreshAll(); }
  catch(err){showMessage("#reportMessage",err.message);}
});
$("#refreshBtn").addEventListener("click",refreshAll);
$("#statusFilter").addEventListener("change",loadReports);
function showAuth(){
  $("#authPanel").classList.remove("hidden"); $("#appPanel").classList.add("hidden");
  $("#userArea").textContent=""; authTab("login");
}
async function showApp(){
  $("#authPanel").classList.add("hidden"); $("#appPanel").classList.remove("hidden");
  $("#userArea").textContent=currentUser.username;
  $("#welcomeTitle").textContent=`Hello, ${currentUser.username}`;
  $("#roleText").textContent=`Signed in as ${currentUser.role.toLowerCase()}`;
  $("#newReportPanel").classList.toggle("hidden",["POLICE","ADMIN"].includes(currentUser.role));
  await refreshAll();
}
async function refreshAll(){
  try{
    const d=await api("/api/dashboard");
    $("#totalCount").textContent=d.total; $("#pendingCount").textContent=d.pending; $("#resolvedCount").textContent=d.resolved;
    await loadReports();
  }catch(err){if(err.message==="Please log in.")showAuth();else showMessage("#reportMessage",err.message);}
}
async function loadReports(){
  try{
    const status=$("#statusFilter").value;
    const reports=await api("/api/reports"+(status?"?status="+encodeURIComponent(status):""));
    const root=$("#reportsList");
    if(!reports.length){root.innerHTML='<div class="empty">No reports found.</div>';return;}
    root.innerHTML=reports.map(r=>`
      <article class="reportCard">
        <div class="reportTop"><div><div class="reportTitle">#${r.id} · ${safe(r.eventType)}</div><div class="reportMeta">Submitted by ${safe(r.reporterUsername)} · Incident: ${safe(r.dateTime?.replace("T"," ")||"—")}</div></div><span class="pill ${safe(r.status)}">${safe(r.status.replaceAll("_"," "))}</span></div>
        <div class="reportMeta">Location: ${safe(r.loc)}</div>
        <div class="reportDescription">${safe(r.description)}</div>
        ${r.firNumber?`<div class="reportMeta"><strong>Official FIR number:</strong> ${safe(r.firNumber)}</div>`:""}
        ${["POLICE","ADMIN"].includes(currentUser.role)?`<div class="staffActions"><select id="status-${r.id}" aria-label="New status for report ${r.id}"><option>PENDING</option><option>UNDER_REVIEW</option><option>INVESTIGATING</option><option>RESOLVED</option><option>CLOSED</option></select>${currentUser.role==="ADMIN"?`<input id="fir-${r.id}" placeholder="FIR number (optional)" aria-label="FIR number">`:""}<button class="secondary" onclick="updateReport(${r.id})">Save update</button></div>`:""}
      </article>`).join("");
    reports.forEach(r=>{const s=$(`#status-${r.id}`);if(s)s.value=r.status;});
  }catch(err){$("#reportsList").innerHTML=`<div class="empty">${safe(err.message)}</div>`;}
}
async function updateReport(id){
  const body={status:$(`#status-${id}`).value};
  const fir=$(`#fir-${id}`);if(fir)body.firNumber=fir.value;
  try{await api(`/api/reports/${id}/status`,{method:"PATCH",body:JSON.stringify(body)});await refreshAll();}
  catch(err){alert(err.message);}
}
(async()=>{try{currentUser=await api("/api/auth/me");await showApp();}catch(_){showAuth();}})();
