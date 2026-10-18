#!/usr/bin/env bash
# ==============================================================================
# review-issues.sh — Herramienta agéntica para gestión y resolución de Issues
# Repositorio: https://github.com/laralbir/md-gh-navigator/issues
# ==============================================================================

set -euo pipefail

REPO="laralbir/md-gh-navigator"

show_help() {
    cat <<EOF
Uso: $0 <comando> [argumentos]

Comandos disponibles:
  list                          Lista todos los issues abiertos en formato JSON/tabular
  view <numero>                 Muestra el detalle completo de un issue y sus comentarios
  analyze <numero>              Muestra la información relevante del issue para análisis de causa raíz
  comment <numero> <mensaje>    Publica un comentario en el issue especificado
  close <numero> <mensaje>      Publica un comentario de cierre y cierra el issue como completado
  wontfix <numero> <mensaje>    Publica un comentario y cierra el issue como 'not_planned'
  reopen <numero> [mensaje]     Reabre un issue cerrado con un comentario opcional
  label <numero> <etiqueta>     Añade una etiqueta al issue

Ejemplos:
  $0 list
  $0 view 12
  $0 comment 12 "Causa raíz identificada en ReaderViewModel.kt. Solución enviada en PR #2."
  $0 close 12 "Solucionado mediante commit 8adb291. Cerrando issue."
EOF
}

check_gh() {
    if ! command -v gh &> /dev/null; then
        echo "Error: La herramienta 'gh' (GitHub CLI) no está instalada o no se encuentra en el PATH." >&2
        exit 1
    fi
}

cmd="${1:-help}"

case "$cmd" in
    list)
        check_gh
        echo "🔍 Listando issues abiertos en $REPO..."
        gh issue list --repo "$REPO" --state open --json number,title,author,labels,createdAt,updatedAt,comments \
            --template '{{range .}}#{{.number}} | {{.title}} | Autor: {{.author.login}} | Comentarios: {{len .comments}} | Creado: {{.createdAt}}{{"\n"}}{{end}}'
        ;;
    view)
        check_gh
        if [[ $# -lt 2 ]]; then
            echo "Error: Debes especificar el número de issue. Ejemplo: $0 view 1" >&2
            exit 1
        fi
        issue_num="$2"
        echo "📖 Detalles del Issue #$issue_num en $REPO:"
        gh issue view "$issue_num" --repo "$REPO" --comments
        ;;
    analyze)
        check_gh
        if [[ $# -lt 2 ]]; then
            echo "Error: Debes especificar el número de issue. Ejemplo: $0 analyze 1" >&2
            exit 1
        fi
        issue_num="$2"
        echo "🔬 Extrayendo datos para Análisis de Causa Raíz (RCA) del Issue #$issue_num:"
        gh issue view "$issue_num" --repo "$REPO" --json number,title,body,author,labels,comments
        ;;
    comment)
        check_gh
        if [[ $# -lt 3 ]]; then
            echo "Error: Uso: $0 comment <numero> <mensaje>" >&2
            exit 1
        fi
        issue_num="$2"
        comment_msg="$3"
        echo "💬 Publicando comentario en el Issue #$issue_num..."
        gh issue comment "$issue_num" --repo "$REPO" --body "$comment_msg"
        echo "✅ Comentario publicado exitosamente en #$issue_num."
        ;;
    close)
        check_gh
        if [[ $# -lt 3 ]]; then
            echo "Error: Uso: $0 close <numero> <mensaje_cierre>" >&2
            exit 1
        fi
        issue_num="$2"
        comment_msg="$3"
        echo "🔒 Cerrando el Issue #$issue_num con resolución..."
        gh issue close "$issue_num" --repo "$REPO" --comment "$comment_msg" --reason "completed"
        echo "✅ Issue #$issue_num cerrado como completado."
        ;;
    wontfix)
        check_gh
        if [[ $# -lt 3 ]]; then
            echo "Error: Uso: $0 wontfix <numero> <motivo>" >&2
            exit 1
        fi
        issue_num="$2"
        comment_msg="$3"
        echo "🚫 Cerrando el Issue #$issue_num como 'not_planned'..."
        gh issue close "$issue_num" --repo "$REPO" --comment "$comment_msg" --reason "not planned"
        echo "✅ Issue #$issue_num cerrado como no planificado."
        ;;
    reopen)
        check_gh
        if [[ $# -lt 2 ]]; then
            echo "Error: Uso: $0 reopen <numero> [mensaje]" >&2
            exit 1
        fi
        issue_num="$2"
        optional_msg="${3:-Reabierto para investigación adicional}"
        echo "🔓 Reabriendo el Issue #$issue_num..."
        gh issue reopen "$issue_num" --repo "$REPO" --comment "$optional_msg"
        echo "✅ Issue #$issue_num reabierto."
        ;;
    label)
        check_gh
        if [[ $# -lt 3 ]]; then
            echo "Error: Uso: $0 label <numero> <etiqueta>" >&2
            exit 1
        fi
        issue_num="$2"
        label_name="$3"
        echo "🏷️ Añadiendo etiqueta '$label_name' al Issue #$issue_num..."
        gh issue edit "$issue_num" --repo "$REPO" --add-label "$label_name"
        echo "✅ Etiqueta añadida."
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo "Comando desconocido: $cmd" >&2
        show_help
        exit 1
        ;;
esac
