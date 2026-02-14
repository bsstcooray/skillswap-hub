async function postJson(url, data){
  const res = await fetch(url, {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify(data)
  });
  const text = await res.text();
  try { return {ok: res.ok, status: res.status, body: JSON.parse(text)}; }
  catch { return {ok: res.ok, status: res.status, body: text}; }
}

function qs(name){
  const u = new URL(window.location.href);
  return u.searchParams.get(name);
}
