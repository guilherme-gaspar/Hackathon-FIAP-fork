# Roteiro resumido e natural — SUS Flow

**Duração estimada:** 5 minutos e 30 segundos a 6 minutos e 30 segundos.
**Como usar:** memorize apenas a ideia principal de cada slide. As frases abaixo são um apoio; não precisam ser repetidas palavra por palavra.

## Slide 1 — Abertura

### Ideia principal

Apresentar a equipe e resumir a proposta em uma frase.

### Fala sugerida

> Olá! Nós somos Guilherme de Castro Gaspar e Natan Santos Bastos, e este é o SUS Flow.
>
> A nossa ideia é tornar a triagem mais organizada e ágil, usando inteligência artificial como apoio — mas mantendo a decisão clínica com o profissional de saúde.

### Gancho

> Para entender a solução, primeiro precisamos olhar para o problema que escolhemos resolver.

---

## Slide 2 — O problema

### Ideia principal

O profissional precisa coletar informações importantes rapidamente, mesmo quando o paciente relata a queixa de maneira vaga.

### Fala sugerida

> No acolhimento, o profissional recebe pessoas com queixas muito diferentes e precisa agir rápido.
>
> Um paciente pode dizer apenas que está com dor no peito e passando mal. A partir disso, é necessário fazer as perguntas certas, conferir os sinais vitais e identificar a urgência.
>
> Quando essas informações ficam incompletas ou espalhadas, aumenta o retrabalho e fica mais difícil manter um fluxo padronizado até o encaminhamento.

### Palavra-chave

**Acolher, priorizar e encaminhar.**

---

## Slide 3 — A solução

### Ideia principal

Mostrar que o SUS Flow é um fluxo integrado, não apenas um chatbot.

### Fala sugerida

> O SUS Flow conecta cadastro de pacientes, triagem e agendamento em uma única API.
>
> O profissional registra a queixa, recebe sugestões da IA, revisa essas sugestões e informa os sinais vitais e sintomas confirmados.
>
> Depois, o sistema classifica o risco por regras e permite criar um agendamento considerando a prioridade da triagem.
>
> Então, não é apenas uma conversa com IA. É um fluxo completo, do acolhimento ao próximo cuidado.

---

## Slide 4 — O diferencial da IA

### Ideia principal

A IA ajuda a organizar a entrevista, mas não diagnostica nem decide o risco.

### Fala sugerida

> O nosso principal diferencial está na forma responsável de usar inteligência artificial.
>
> O Ollama analisa a queixa e pode sugerir sintomas, perguntas complementares, alertas e campos que ainda precisam ser preenchidos.
>
> Mas a IA não dá diagnóstico, não prescreve e não escolhe a classificação do paciente. O profissional revisa os dados, e o risco é calculado por regras determinísticas.

### Frase para destacar

> A IA ajuda a lembrar; as regras ajudam a manter consistência; e o profissional continua no controle.

---

## Slide 5 — Como funciona

### Ideia principal

Explicar rapidamente o fluxo apresentado na imagem.

### Fala sugerida

> O fluxo é este: recebemos a queixa, a IA organiza sugestões, o profissional valida, as regras classificam o risco e o sistema permite o encaminhamento para a agenda.
>
> Se o Ollama estiver indisponível, a triagem continua funcionando. A aplicação simplesmente segue sem as sugestões da IA.
>
> No vídeo do MVP, mostramos esse caminho completo pelo Postman: login, paciente, análise da queixa, triagem e agendamento.

---

## Slide 6 — MVP e arquitetura

### Ideia principal

Apresentar as tecnologias e explicar a arquitetura sem aprofundar demais.

### Fala sugerida

> O MVP foi desenvolvido com Java 21 e Spring Boot. Usamos PostgreSQL e Flyway, autenticação JWT, Docker, Swagger, Postman e Ollama.
>
> A organização é inspirada em Arquitetura Hexagonal, ou Ports and Adapters.
>
> Na prática, isso significa que as regras de negócio ficam no domínio, enquanto banco de dados e IA ficam isolados em adaptadores.
>
> Assim, podemos desligar ou trocar o Ollama sem alterar a regra que classifica o risco.

### Frase para destacar

> A IA está na borda da arquitetura; a decisão de risco fica no centro do domínio.

---

## Slide 7 — Impacto esperado

### Ideia principal

Falar em hipótese e métricas, sem prometer resultados ainda não validados.

### Fala sugerida

> Como este é um MVP, nós não queremos afirmar um impacto clínico sem realizar um piloto.
>
> O que propomos é medir o tempo de triagem, a completude dos dados, a aceitação das sugestões da IA e a rastreabilidade do encaminhamento.
>
> Para o profissional, esperamos oferecer uma conferência mais organizada. Para o paciente, esperamos criar um caminho mais coerente entre acolhimento, prioridade e próximo atendimento.

---

## Slide 8 — Próximos passos e encerramento

### Ideia principal

Apresentar uma evolução realista e terminar retomando a proposta central.

### Fala sugerida

> Como próximos passos, faríamos um piloto em uma UBS ou UPA, incluiríamos auditoria das respostas da IA, autorização por unidade de saúde e métricas operacionais.
>
> Também seria necessária a validação das regras por especialistas. O nosso MVP não pretende ser um protocolo clínico certificado; ele demonstra uma arquitetura viável para apoiar esse processo.
>
> Em resumo, o SUS Flow usa a IA para organizar informações, regras para garantir consistência e profissionais para tomar decisões.
>
> Obrigado! Agora vamos mostrar o MVP funcionando.

---

## Cola rápida — uma frase por slide

1. **Proposta:** IA como apoio, profissional como responsável pela decisão.
2. **Problema:** informações precisam ser coletadas rapidamente e de forma organizada.
3. **Solução:** paciente, triagem e agenda em um único fluxo.
4. **Diferencial:** Ollama sugere; regras classificam; profissional valida.
5. **Fluxo:** queixa → IA → revisão → risco → agendamento.
6. **Arquitetura:** domínio independente, PostgreSQL e Ollama como adaptadores.
7. **Impacto:** medir tempo, completude, uso da IA e encaminhamento.
8. **Próximos passos:** piloto, auditoria, validação clínica e evolução do produto.

## Dicas para soar natural

- Leia o roteiro duas ou três vezes e depois grave usando apenas a seção **Cola rápida**.
- Olhe para a câmera no início e no encerramento de cada slide.
- Faça pequenas pausas; não tente preencher todo silêncio.
- Troque palavras durante a fala se isso deixar a frase mais confortável.
- Se esquecer um trecho, retome pela ideia principal em vez de voltar e repetir.
