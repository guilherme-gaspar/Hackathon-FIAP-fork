# Roteiro de pitch — SUS Flow

**Duração prevista:** 7 minutos e 30 segundos.
**Uso:** abrir `Pitch-SUS-Flow.html` no navegador, usar as setas para avançar e gravar a tela/apresentação.

## Slide 1 — Abertura (0:00–0:50)

> Olá, nós somos Guilherme de Castro Gaspar e Natan Santos Bastos, e este é o SUS Flow. Nosso projeto nasceu de uma pergunta simples: como usar tecnologia para tornar a triagem mais ágil sem retirar do profissional de saúde a decisão clínica?
> Nossa resposta é um copiloto de acolhimento que ajuda a transformar a queixa do paciente em informações estruturadas, sugere pontos para revisão e conecta a triagem ao próximo cuidado. A decisão de risco continua humana, transparente e baseada em regras.

**Transição:** “Antes de falar da solução, vamos contextualizar a dor que ela enfrenta.”

## Slide 2 — Problema (0:50–1:45)

> No atendimento público, o acolhimento precisa equilibrar velocidade e segurança. Um profissional recebe uma queixa em linguagem livre, precisa coletar sinais vitais, identificar sintomas relevantes e definir o próximo passo.
> Imagine uma pessoa chegando com dor no peito e dizendo apenas que está se sentindo mal. Em poucos minutos, o profissional precisa investigar sintomas associados, conferir sinais vitais e identificar critérios de urgência. Quando informações ficam incompletas ou dispersas, o processo perde padronização e pode gerar retrabalho.
> E o desafio não termina na classificação: depois da triagem, o encaminhamento e o agendamento também precisam acompanhar a prioridade do paciente. Foi nesse recorte — acolher, priorizar e encaminhar — que concentramos o nosso MVP.

**Mensagem-chave:** não alegar que a solução resolve todo o SUS; delimitar o foco ao acolhimento, triagem e encaminhamento.

## Slide 3 — Solução (1:45–2:40)

> O SUS Flow reúne três etapas em um único fluxo de API: cadastro e consulta de pacientes, triagem clínica e agendamento.
> O profissional registra a queixa e pode usar a IA para organizar o contexto antes de concluir a coleta. Depois de revisar as sugestões, informa os sinais vitais e os sintomas confirmados. O sistema aplica regras determinísticas, registra a triagem e pode apoiar o agendamento de consulta ou exame considerando a prioridade derivada desse atendimento.
> Assim, não criamos apenas um chatbot. Criamos um fluxo de back-end integrado, no qual cada etapa produz dados úteis para a etapa seguinte e mantém a decisão profissional no centro.

**Transição:** “O principal diferencial está em como usamos inteligência artificial nesse processo.”

## Slide 4 — IA responsável (2:40–3:45)

> Nossa IA não dá diagnóstico e não escolhe a cor de risco do paciente. Ela atua como assistente do profissional.
> Com Ollama executado localmente, a aplicação analisa a queixa e o contexto informado e retorna sintomas sugeridos, perguntas complementares, alertas para conferência e campos possivelmente ausentes.
> Em seguida, o profissional valida esses dados. Somente depois as regras determinísticas do domínio classificam o risco. Isso torna a decisão rastreável e evita delegar uma decisão clínica ao modelo de linguagem.
> A execução local também demonstra uma alternativa em que os dados não precisam ser enviados a um serviço público de IA na internet. Para um uso real, ainda seriam necessárias validação clínica, governança, auditoria e adequação à LGPD; no MVP, mostramos a separação técnica necessária para essa evolução.

**Frase de impacto:** “A IA reduz esquecimento; as regras garantem consistência; o profissional mantém a autonomia.”

## Slide 5 — Fluxo (3:45–4:35)

> Este é o fluxo completo: a queixa entra no sistema, a IA organiza sugestões, o profissional valida, as regras classificam o risco e o sistema permite o encaminhamento para a agenda.
> Caso o Ollama não esteja disponível, o sistema degrada de forma segura: não interrompe a triagem e retorna a análise vazia. A operação essencial continua funcionando.
> Na demonstração do MVP, esse caminho aparece de ponta a ponta no Postman: autenticação, cadastro do paciente, análise da queixa, registro da triagem e criação do agendamento. Os identificadores e o token são reaproveitados automaticamente pela collection.

## Slide 6 — MVP e arquitetura (4:35–5:35)

> O MVP foi construído com Java 21 e Spring Boot. Utilizamos PostgreSQL e Flyway para persistência e versionamento do banco, JWT para autenticação, Docker para executar o ambiente completo e OpenAPI e Postman para documentar e demonstrar a API.
> A solução utiliza uma arquitetura inspirada em Arquitetura Hexagonal, também conhecida como Ports and Adapters. Os controllers funcionam como adaptadores de entrada, os casos de uso organizam o fluxo da aplicação, o domínio concentra as regras de negócio e as portas de saída isolam integrações como PostgreSQL e Ollama.
> Com essa separação, a regra de classificação de risco não depende da inteligência artificial. O Ollama permanece na borda da arquitetura como um adaptador assistivo e pode ser desligado ou substituído sem interromper a funcionalidade principal da triagem.

**Frase de impacto:** “A IA está na borda da arquitetura; a decisão de risco permanece no centro do domínio.”

## Slide 7 — Impacto e validação (5:35–6:30)

> Não queremos prometer impacto clínico sem um piloto real. Por isso, definimos métricas que devem ser medidas em uma implantação controlada: tempo até a classificação, completude dos dados da triagem e rastreabilidade do encaminhamento.
> A hipótese é que o copiloto reduza omissões no acolhimento e ajude a padronizar a coleta de informações, sempre com revisão humana. Também mediríamos a latência e a disponibilidade da IA, quantas sugestões são aceitas ou rejeitadas e quantas vezes o fallback é acionado.
> O benefício esperado para o profissional é um roteiro de conferência mais organizado. Para o paciente, é um fluxo mais coerente entre acolhimento, prioridade e encaminhamento. Esses resultados precisariam ser comprovados em parceria com uma unidade de saúde.

## Slide 8 — Próximos passos e encerramento (6:30–7:30)

> Como próximos passos, evoluiríamos para um piloto em uma UBS ou UPA, com autorização baseada na unidade de saúde, histórico clínico, auditoria das sugestões de IA e observabilidade de métricas operacionais.
> Também submeteríamos as regras de classificação à validação formal de especialistas e estudaríamos interoperabilidade com sistemas de saúde. Nosso MVP não se apresenta como um protocolo clínico certificado; ele demonstra uma arquitetura viável e responsável para apoiar esse processo.
> O SUS Flow mostra que inovação não precisa significar entregar a decisão à inteligência artificial. Pode significar usar a IA para organizar informações, regras para garantir consistência e profissionais para exercer julgamento. Tecnologia para apoiar quem cuida e tornar o atendimento mais organizado, seguro e humano. Obrigado — agora vamos demonstrar o sistema funcionando.

## Dicas de gravação

1. Utilizem tela cheia no navegador (`F11`) e avancem com as setas.
2. Evitem ler os slides: eles são visuais; a narrativa está neste roteiro.
3. Reservem aproximadamente 30 segundos de margem para pausas e trocas de apresentador.
4. Após o pitch, gravem separadamente o vídeo de MVP usando a collection Postman.
