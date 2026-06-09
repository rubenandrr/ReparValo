import json
import urllib.request
import urllib.error

def main():
    print("--- TEST DIRECT DE CLE API GEMINI ---")
    api_key = input("Veuillez coller votre nouvelle cle API Gemini (AIzaSy...) : ").strip()
    
    if not api_key:
        print("Aucune cle fournie.")
        return

    if not api_key.startswith("AIzaSy"):
        print("Attention : Les cles API Gemini d'AI Studio commencent normalement par 'AIzaSy'.")

    # Mask key for display
    masked = api_key[:6] + "..." + api_key[-6:] if len(api_key) > 12 else api_key
    print(f"Verification de la cle : {masked} (Longueur : {len(api_key)} caracteres)")

    # Call Gemini API
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={api_key}"
    headers = {"Content-Type": "application/json"}
    body = {
        "contents": [{
            "parts": [{"text": "Dit 'Bonjour' en un seul mot."}]
        }]
    }

    req = urllib.request.Request(
        url,
        data=json.dumps(body).encode("utf-8"),
        headers=headers,
        method="POST"
    )

    print("\nConnexion aux serveurs de Google Gemini...")
    try:
        with urllib.request.urlopen(req) as response:
            res_body = json.loads(response.read().decode("utf-8"))
            text = res_body["candidates"][0]["content"]["parts"][0]["text"].strip()
            print("\nSUCCESS ! La cle API fonctionne parfaitement.")
            print(f"Gemini a repondu : {text}")
    except urllib.error.HTTPError as e:
        print(f"\nERREUR HTTP renvoyee par Google (Code {e.code}) :")
        try:
            err_json = json.loads(e.read().decode("utf-8"))
            print(f"Message d'erreur : {err_json['error']['message']}")
            print(f"Statut technique : {err_json['error']['status']}")
        except Exception:
            print(f"Detail : {e.reason}")
    except Exception as e:
        print(f"\nImpossible de faire l'appel : {e}")

if __name__ == "__main__":
    main()
