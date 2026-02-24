# Read PDF and extract text streams
$content = Get-Content -Path "C:\Users\abram\Downloads\smartphonesProject-5.pdf" -Raw -Encoding Byte
$text = [System.Text.Encoding]::ASCII.GetString($content)

# Find all text between BT and ET markers (PDF text objects)
$textObjects = [regex]::Matches($text, 'BT\s*(.*?)\s*ET', [System.Text.RegularExpressions.RegexOptions]::Singleline)

foreach ($obj in $textObjects) {
    $inner = $obj.Groups[1].Value
    # Extract text from Tj and TJ operators
    $tjMatches = [regex]::Matches($inner, '\(([^\)]*)\)\s*Tj')
    foreach ($tj in $tjMatches) {
        Write-Output $tj.Groups[1].Value
    }
    # Also try TJ arrays
    $tjArrayMatches = [regex]::Matches($inner, '\(([^\)]*)\)')
    foreach ($tja in $tjArrayMatches) {
        $val = $tja.Groups[1].Value
        if ($val.Length -gt 1) {
            Write-Host $val -NoNewline
        }
    }
    Write-Host ""
}
