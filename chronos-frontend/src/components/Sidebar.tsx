import Link from "next/link";
import { Clock, LayoutDashboard, Settings, Activity } from "lucide-react";

export default function Sidebar() {
  return (
    <div className="w-64 h-full bg-slate-950 flex flex-col pt-8 pb-4">
      <div className="px-6 flex items-center space-x-3 mb-10">
        <div className="w-8 h-8 rounded-full bg-indigo-600 flex items-center justify-center">
          <Clock size={18} className="text-white" />
        </div>
        <h1 className="text-xl font-bold bg-gradient-to-r from-indigo-400 to-cyan-400 bg-clip-text text-transparent tracking-tight">
          CHRONOS
        </h1>
      </div>

      <nav className="flex-1 px-4 space-y-2">
        <Link
          href="/"
          className="flex items-center space-x-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-900 transition-all group"
        >
          <LayoutDashboard size={18} className="group-hover:text-indigo-400 transition-colors" />
          <span className="font-medium text-sm">Dashboard</span>
        </Link>
        <Link
          href="/timeline/demo"
          className="flex items-center space-x-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-900 transition-all group"
        >
          <Activity size={18} className="group-hover:text-indigo-400 transition-colors" />
          <span className="font-medium text-sm">Timelines</span>
        </Link>
      </nav>

      <div className="px-4">
        <Link
          href="/settings"
          className="flex items-center space-x-3 px-3 py-2.5 rounded-lg text-slate-500 hover:text-white hover:bg-slate-900 transition-all group"
        >
          <Settings size={18} className="group-hover:text-slate-300 transition-colors" />
          <span className="font-medium text-sm">Settings</span>
        </Link>
      </div>
    </div>
  );
}
