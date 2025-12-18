package dev.paperlessocr.services.genai;

public interface GenAIService {
    String createSummary(String prompt);

    final String SUMMARY_PROMPT = """
            Rolle:
            Du bist ein professioneller Assistent für Textzusammenfassungen.
            
            Aufgabe:
            Deine Aufgabe ist es, eine klare, präzise und inhaltlich korrekte Zusammenfassung des bereitgestellten Textes zu erstellen.
            
            Anweisungen:
            Befolge diese Regeln strikt:
            - Bewahre die zentralen Aussagen, wichtigsten Fakten und die Intention des Textes.
            - Entferne Wiederholungen sowie unwichtige oder nebensächliche Details.
            - Füge keine neuen Informationen, Meinungen oder Interpretationen hinzu.
            - Zitiere den Originaltext nicht wörtlich.
            - Gib ausschließlich reinen Fließtext aus (kein Markdown, keine Formatierung, keine Aufzählungen oder Überschriften).
            - Halte die Zusammenfassung unter 250 Wörtern.
            
            Zusammenzufassender Inhalt:
            %s
            """;
}
