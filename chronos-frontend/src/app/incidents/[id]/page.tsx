"use client";

import { useEffect, useState } from "react";
import { AlertTriangle, Clock, Activity, CheckCircle, XCircle, FileText, Zap, ShieldAlert, AlertCircle, Search } from "lucide-react";

export default function IncidentReportPage({ params }: { params: { id: string } }) {
  const [report, setReport] = useState<any>(null);
  const [investigation, setInvestigation] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [investigating, setInvestigating] = useState(true);

  useEffect(() => {
    // Phase 10: Deterministic Evidence Dossier
    setTimeout(() => {
      setReport({
        incidentId: "INC-" + params.id.substring(0, 8),
        failedEventId: params.id,
        outcome: "ORDER_FAILED",
        rootCauseAttribution: "Root Cause Candidate",
        faultAnalysis: ["LATENCY-01"],
        traceImpact: "+3000 ms causal delay",
        causalChain: [
          "ORDER_CREATED",
          "PAYMENT_STARTED",
          "PAYMENT_SUCCESS",
          "INVENTORY_TIMEOUT",
          "ORDER_FAILED"
        ],
        counterfactualProof: {
          status: "PROVEN", // test with AMBIGUOUS or NOT_AVAILABLE
          removedFaults: ["LATENCY-01"],
          whatIfOutcome: "ORDER_COMPLETED",
          realDurationMs: 3500,
          whatIfDurationMs: 500
        },
        evidenceMetrics: {
          totalEvents: 7,
          totalFaults: 1,
          causalEdges: 6
        },
        evidenceChain: [
            { evidenceId: "E1", description: "PAYMENT_STARTED" },
            { evidenceId: "E2", description: "FAULT_TRIGGERED (LATENCY-01)" },
            { evidenceId: "E3", description: "PAYMENT_SUCCESS" },
            { evidenceId: "E4", description: "INVENTORY_TIMEOUT" },
            { evidenceId: "E5", description: "ORDER_FAILED" },
            { evidenceId: "E6", description: "EXP-002: LATENCY-01 removed" },
            { evidenceId: "E7", description: "INVENTORY_RESERVED" },
            { evidenceId: "E8", description: "ORDER_COMPLETED" }
        ]
      });
      setLoading(false);
      
      // Phase 11: Mock AI Investigator
      setTimeout(() => {
        setInvestigation({
          summary: "The order failed after a fault-induced delay caused the inventory reservation to time out.",
          facts: [
            "LATENCY-01 fired on payment-service",
            "+3000ms observed causal delay",
            "Inventory timeout logged",
            "ORDER_FAILED final state"
          ],
          inferences: [
            "The injected latency directly contributed to the failure.",
            "Removing the fault allowed the workflow to complete successfully."
          ],
          unknowns: [
            "Chronos cannot determine whether the remaining 500ms inter-service delay was network or queue latency."
          ],
          evidenceQuality: {
            evidenceCompleteness: "HIGH",
            counterfactualValidation: "AVAILABLE",
            directFaultAttribution: "YES",
            causalChain: "COMPLETE"
          }
        });
        setInvestigating(false);
      }, 1500);

    }, 1000);
  }, [params.id]);

  if (loading) {
    return <div className="flex h-full items-center justify-center text-slate-400 font-mono"><Search className="mr-2 animate-spin text-indigo-500" /> Gathering Deterministic Evidence...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto py-10 w-full">
      <div className="mb-8">
        <h1 className="text-3xl font-black text-white flex items-center">
          <ShieldAlert className="mr-3 text-rose-500" size={32} />
          {report.incidentId}
          <span className="ml-4 px-3 py-1 bg-indigo-500/10 text-indigo-400 rounded-md text-xs font-bold border border-indigo-500/20 uppercase">
            Phase 11
          </span>
        </h1>
        <h2 className="text-xl text-rose-400 font-mono mt-2">{report.outcome}</h2>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 col-span-1 md:col-span-1">
          <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-4">Root Cause</h3>
          <div className="text-2xl font-bold text-white mb-1">
            {report.faultAnalysis && report.faultAnalysis.length > 0 ? report.faultAnalysis[0] : "Unknown"}
          </div>
          <div className="text-sm text-rose-400 font-mono">{report.traceImpact}</div>
          <div className="mt-4 inline-block px-2 py-1 bg-amber-500/10 border border-amber-500/20 text-amber-500 text-[10px] rounded uppercase font-bold">
            {report.rootCauseAttribution}
          </div>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 col-span-1 md:col-span-2">
          <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-4">Counterfactual Proof</h3>
          
          {report.counterfactualProof.status === "PROVEN" ? (
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-slate-950 p-4 rounded-lg border border-rose-500/20">
                <div className="text-slate-500 text-xs uppercase font-bold mb-2">Real</div>
                <div className="text-rose-400 font-bold mb-1 flex items-center">
                  <XCircle size={14} className="mr-1.5" /> FAILED
                </div>
                <div className="text-slate-400 font-mono text-sm">{report.counterfactualProof.realDurationMs} ms</div>
              </div>
              <div className="bg-slate-950 p-4 rounded-lg border border-emerald-500/20">
                <div className="text-slate-500 text-xs uppercase font-bold mb-2">What-If</div>
                <div className="text-emerald-400 font-bold mb-1 flex items-center">
                  <CheckCircle size={14} className="mr-1.5" /> {report.counterfactualProof.whatIfOutcome}
                </div>
                <div className="text-slate-400 font-mono text-sm">{report.counterfactualProof.whatIfDurationMs} ms</div>
              </div>
              <div className="col-span-2 mt-2 flex items-center text-sm text-slate-300">
                <Zap size={14} className="text-indigo-400 mr-2" />
                Fault removed: <span className="font-mono text-indigo-300 ml-2">{report.counterfactualProof.removedFaults?.join(", ")}</span>
              </div>
            </div>
          ) : report.counterfactualProof.status === "AMBIGUOUS" ? (
            <div className="flex flex-col items-center justify-center h-full text-amber-500">
              <AlertTriangle size={24} className="mb-2" />
              <p className="font-bold text-sm">Ambiguous Experiments</p>
              <p className="text-xs text-slate-400 mt-1">Multiple counterfactuals match this fault.</p>
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center h-full text-slate-500">
              <AlertCircle size={24} className="mb-2" />
              <p className="font-bold text-sm">Not Available</p>
              <p className="text-xs text-slate-600 mt-1">No counterfactual experiment run for this incident.</p>
            </div>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6">
          <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-6">Causal Chain</h3>
          <ul className="space-y-4 relative before:absolute before:inset-y-0 before:left-[11px] before:w-0.5 before:bg-slate-800">
            {report.causalChain.map((evt: string, i: number) => (
              <li key={i} className="flex items-center relative z-10 pl-8">
                <div className={`absolute left-1 w-2.5 h-2.5 rounded-full ring-4 ring-slate-900 ${i === report.causalChain.length - 1 ? "bg-rose-500 ring-rose-500/20" : "bg-slate-600"}`}></div>
                <span className={`font-mono text-sm ${i === report.causalChain.length - 1 ? "text-rose-400 font-bold" : "text-slate-300"}`}>
                  {evt}
                </span>
              </li>
            ))}
          </ul>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 flex flex-col">
          <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-6">Evidence</h3>
          
          <div className="flex space-x-6 mb-6 pb-6 border-b border-slate-800">
            <div>
              <div className="text-2xl font-bold text-white">{report.evidenceMetrics?.totalEvents || 0}</div>
              <div className="text-[10px] text-slate-500 uppercase font-bold">Events</div>
            </div>
            <div>
              <div className="text-2xl font-bold text-white">{report.evidenceMetrics?.totalFaults || 0}</div>
              <div className="text-[10px] text-slate-500 uppercase font-bold">Faults</div>
            </div>
            <div>
              <div className="text-2xl font-bold text-white">{report.evidenceMetrics?.causalEdges || 0}</div>
              <div className="text-[10px] text-slate-500 uppercase font-bold">Causal Edges</div>
            </div>
          </div>

          <div className="flex-1 overflow-y-auto pr-2">
            <h4 className="text-[10px] text-slate-500 uppercase font-bold mb-3">Evidence Chain</h4>
            <div className="space-y-2">
              {report.evidenceChain.map((ev: any, i: number) => (
                <div key={i} className="bg-slate-950 p-2.5 rounded border border-slate-800 flex items-start">
                  <div className="text-xs font-bold text-slate-500 mr-3 w-6">{ev.evidenceId}</div>
                  <div className="text-xs font-mono text-slate-300">{ev.description}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* PHASE 11: AI INVESTIGATION UI */}
      <div className="border-t border-slate-800 pt-8 mt-4">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-black text-white flex items-center">
              <Zap className="mr-3 text-indigo-500" size={28} />
              AI INVESTIGATION
            </h2>
            <div className="text-xs font-bold text-indigo-400 uppercase mt-2 inline-flex items-center bg-indigo-500/10 px-2.5 py-1 rounded border border-indigo-500/20">
              Generated from Chronos evidence
            </div>
          </div>
        </div>

        {investigating ? (
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-12 flex flex-col items-center justify-center text-indigo-400">
            <Search className="animate-spin mb-4" size={32} />
            <div className="font-mono text-sm">Reasoning over Deterministic Evidence...</div>
          </div>
        ) : investigation ? (
          <div className="bg-slate-900 border border-indigo-500/30 rounded-xl overflow-hidden shadow-lg shadow-indigo-900/20">
            <div className="p-6 border-b border-slate-800 bg-slate-950/30">
              <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-3">Summary</h3>
              <p className="text-slate-200 text-lg leading-relaxed">{investigation.summary}</p>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2">
              <div className="p-6 border-r border-slate-800 bg-slate-950/10">
                <h3 className="text-emerald-500 text-xs font-bold uppercase tracking-wider mb-1">Facts</h3>
                <p className="text-slate-500 text-[10px] uppercase mb-5">Directly observed by Chronos</p>
                <ul className="space-y-4">
                  {investigation.facts.map((fact: string, i: number) => (
                    <li key={i} className="flex items-start text-sm text-slate-300">
                      <span className="text-emerald-500 mr-3 mt-0.5">•</span> 
                      <button 
                        className="text-left hover:text-emerald-400 hover:underline decoration-emerald-500/30 underline-offset-4 transition-colors cursor-pointer group"
                        title="Click to view original Chronos evidence"
                      >
                        {fact}
                        <span className="inline-block ml-2 opacity-0 group-hover:opacity-100 transition-opacity">
                            <Activity size={12} className="text-emerald-500/50" />
                        </span>
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
              
              <div className="p-6 bg-slate-950/10">
                <h3 className="text-indigo-400 text-xs font-bold uppercase tracking-wider mb-1">Inferences</h3>
                <p className="text-slate-500 text-[10px] uppercase mb-5">Reasoned from Chronos evidence</p>
                <ul className="space-y-4">
                  {investigation.inferences.map((inf: string, i: number) => (
                    <li key={i} className="flex items-start text-sm text-slate-300">
                      <span className="text-indigo-400 mr-3 mt-0.5">•</span> 
                      <button 
                        className="text-left hover:text-indigo-300 hover:underline decoration-indigo-400/30 underline-offset-4 transition-colors cursor-pointer group"
                        title="Click to view derived evidence path"
                      >
                        {inf}
                        <span className="inline-block ml-2 opacity-0 group-hover:opacity-100 transition-opacity">
                            <Zap size={12} className="text-indigo-400/50" />
                        </span>
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
            </div>

            <div className="p-6 border-t border-slate-800 bg-slate-950/50">
              <h3 className="text-amber-500 text-xs font-bold uppercase tracking-wider mb-1">Unknown / Not Established</h3>
              <p className="text-slate-500 text-[10px] uppercase mb-4">Not established by available evidence</p>
              <ul className="space-y-3">
                {investigation.unknowns.map((unk: string, i: number) => (
                  <li key={i} className="flex items-start text-sm text-slate-400">
                    <span className="text-amber-500 mr-3 mt-0.5">•</span> 
                    {unk}
                  </li>
                ))}
              </ul>
            </div>

            <div className="p-6 border-t border-slate-800 bg-slate-900">
              <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-4">Evidence Quality Metrics</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                <div>
                  <div className="text-slate-500 text-[10px] uppercase font-bold mb-1.5">Evidence Completeness</div>
                  <div className="text-white font-mono text-sm tracking-wide">{investigation.evidenceQuality.evidenceCompleteness}</div>
                </div>
                <div>
                  <div className="text-slate-500 text-[10px] uppercase font-bold mb-1.5">Counterfactual Validation</div>
                  <div className="text-white font-mono text-sm tracking-wide">{investigation.evidenceQuality.counterfactualValidation}</div>
                </div>
                <div>
                  <div className="text-slate-500 text-[10px] uppercase font-bold mb-1.5">Direct Fault Attribution</div>
                  <div className="text-white font-mono text-sm tracking-wide">{investigation.evidenceQuality.directFaultAttribution}</div>
                </div>
                <div>
                  <div className="text-slate-500 text-[10px] uppercase font-bold mb-1.5">Causal Chain</div>
                  <div className="text-white font-mono text-sm tracking-wide">{investigation.evidenceQuality.causalChain}</div>
                </div>
              </div>
            </div>
          </div>
        ) : null}
      </div>

    </div>
  );
}
