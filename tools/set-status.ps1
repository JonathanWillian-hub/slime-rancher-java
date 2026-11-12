# Atualiza o status de uma tarefa no TAREFAS.md
# Uso: .\tools\set-status.ps1 -Id T1.2 -Status ok -Agent "Antigravity" -Note "texto"
# Status: todo | doing | ok | blocked
param(
    [Parameter(Mandatory = $true)][string]$Id,
    [Parameter(Mandatory = $true)][ValidateSet('todo', 'doing', 'ok', 'blocked')][string]$Status,
    [string]$Agent = "",
    [string]$Note = ""
)
$file = Join-Path $PSScriptRoot "..\TAREFAS.md"
$symbols = @{ todo = [string][char]0x2B1C; doing = [char]::ConvertFromUtf32(0x1F504); ok = [string][char]0x2705; blocked = ([string][char]0x26A0 + [char]0xFE0F) }
$lines = [System.IO.File]::ReadAllLines($file, [System.Text.Encoding]::UTF8)
$found = $false
for ($i = 0; $i -lt $lines.Length; $i++) {
    if ($lines[$i].StartsWith("| $Id |")) {
        $cols = $lines[$i].Split('|')
        # cols: 0 vazio,1 ID,2 Status,3 Tarefa,4 Arquivos,5 Depende,6 Criterio,7 Agente,8 Notas,9 vazio
        $cols[2] = " " + $symbols[$Status] + " "
        if ($Agent) { $cols[7] = " $Agent " + (Get-Date -Format 'yyyy-MM-dd') + " " }
        if ($Note) { $cols[8] = " $Note " }
        $lines[$i] = ($cols -join '|')
        $found = $true
    }
}
if (-not $found) { Write-Error "Tarefa $Id nao encontrada"; exit 1 }
[System.IO.File]::WriteAllLines($file, $lines, (New-Object System.Text.UTF8Encoding($false)))
Write-Output "$Id -> $Status"

